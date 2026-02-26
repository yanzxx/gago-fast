
package vip.xiaonuo.auth.modular.sso.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import vip.xiaonuo.auth.api.SaBaseLoginUserApi;
import vip.xiaonuo.auth.core.enums.AuthTerminalTypeEnum;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.modular.login.enums.AuthExceptionEnum;
import vip.xiaonuo.auth.modular.sso.body.LoginTicket2_0Body;
import vip.xiaonuo.auth.modular.sso.config.SsoCasProps;
import vip.xiaonuo.auth.modular.sso.service.SsoCasService;
import vip.xiaonuo.biz.api.BizTabularDisplayApi;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;

@Service
public class SsoCasServiceImpl implements SsoCasService {

    private static final String FAST_TERMINAL_LIMIT_OPEN = "FAST_TERMINAL_LIMIT_OPEN";

    @Resource
    private SsoCasProps casProps;

    @Resource
    private DevConfigApi devConfigApi;

    @Resource
    private BizTabularDisplayApi bizTabularDisplayApi;

    @Resource(name = "loginUserApi")
    private SaBaseLoginUserApi loginUserApi;

    @Override
    public String getLoginRedirectUrl() {
        return casProps.getBaseUrl() + "/sign/authz/cas/login?service=" + casProps.getService();
    }

    @Override
    public String loginTicket2_0(LoginTicket2_0Body body) {
        String url = StrUtil.format(
                casProps.getBaseUrl() + "/sign/authz/cas/serviceValidate?service={}&ticket={}&format={}",
                casProps.getService(), body.getTicket(), "json");
        String result = HttpUtil.get(url);
        System.err.println(">>>>>>>>" + result);
        JSONObject obj = JSONUtil.parseObj(result).getJSONObject("serviceResponse");
        if (obj.containsKey("authenticationFailure")) {
            throw new CommonException("Ticket验证失败: " + obj.getJSONObject("authenticationFailure").toString());
        }
        JSONObject successObj = obj.getJSONObject("authenticationSuccess");
        String account = successObj.getStr("user");
        SaBaseLoginUser saBaseLoginUser = loginUserApi.getUserByAccount(account);
        if (ObjectUtil.isEmpty(saBaseLoginUser)) {
            throw new CommonException(AuthExceptionEnum.ACCOUNT_ERROR.getValue());
        }
        return execLoginB(saBaseLoginUser, AuthTerminalTypeEnum.PC.getValue());
    }

    /**
     * @see vip.xiaonuo.auth.modular.login.service.impl.AuthServiceImpl#execLoginB(SaBaseLoginUser,
     *      String)
     */
    private String execLoginB(SaBaseLoginUser saBaseLoginUser, String device) {
        // 校验状态
        if (!saBaseLoginUser.getEnabled()) {
            throw new CommonException(AuthExceptionEnum.ACCOUNT_DISABLED.getValue());
        }

        // 角色集合
        List<JSONObject> roleList = loginUserApi.getRoleListByUserId(saBaseLoginUser.getId());
        if (CollectionUtil.isEmpty(roleList)) {
            throw new CommonException(4001, "该用户没有分配角色");
        }
        List<String> roleIdList = roleList.stream().map(jsonObject -> jsonObject.getStr("id"))
                .collect(Collectors.toList());

        if (devConfigApi.getValueByKey(FAST_TERMINAL_LIMIT_OPEN) != null
                && (Boolean.parseBoolean(devConfigApi.getValueByKey(FAST_TERMINAL_LIMIT_OPEN)))) {
            // 验证登录终端并返回对应的角色
            // 根据角色ID和登录终端获取允许登录该终端的角色ID
            roleIdList = loginUserApi.getDeviceByRoleIdList(roleIdList, device);
            if (CollectionUtil.isEmpty(roleIdList)) {
                throw new CommonException(4001, "该用户未被授权[" + device + "]登录");
            }
            // 根据可用的角色ID过滤角色
            List<String> finalRoleIdList = roleIdList;
            roleList = roleList.stream().filter(jsonObject -> finalRoleIdList.contains(jsonObject.getStr("id")))
                    .collect(Collectors.toList());
        }

        // 角色码集合
        List<String> roleCodeList = roleList.stream().map(jsonObject -> jsonObject.getStr("code"))
                .collect(Collectors.toList());
        // 角色id和用户id集合
        List<String> userAndRoleIdList = CollectionUtil.unionAll(roleIdList,
                CollectionUtil.newArrayList(saBaseLoginUser.getId()));

        // 执行登录
        StpUtil.login(saBaseLoginUser.getId(),
                new SaLoginModel().setDevice(device).setExtra("name", saBaseLoginUser.getName()));
        // 此处的再一次查询是为了获取上一行登录代码执行后放入数据库的最新登录信息（latestLoginIp 等字段）
        saBaseLoginUser = loginUserApi.getUserByAccount(saBaseLoginUser.getAccount());
        // 获取按钮码
        saBaseLoginUser.setButtonCodeList(loginUserApi.getButtonCodeListListByUserAndRoleIdList(userAndRoleIdList));
        // 获取移动端按钮码
        saBaseLoginUser.setMobileButtonCodeList(
                loginUserApi.getMobileButtonCodeListListByUserIdAndRoleIdList(userAndRoleIdList));
        // 移动端全局权限集合
        saBaseLoginUser
                .setMobileGlobalCodeList(loginUserApi.getMobileGlobalCodeListByUserIdAndRoleIdList(userAndRoleIdList));
        // 获取数据范围
        saBaseLoginUser.setDataScopeList(Convert.toList(SaBaseLoginUser.DataScope.class,
                loginUserApi.getPermissionListByUserIdAndRoleIdList(userAndRoleIdList, saBaseLoginUser.getOrgId())));
        // 获取权限码
        saBaseLoginUser.setPermissionCodeList(saBaseLoginUser.getDataScopeList().stream()
                .map(SaBaseLoginUser.DataScope::getApiUrl).collect(Collectors.toList()));
        // 获取角色码
        saBaseLoginUser.setRoleCodeList(roleCodeList);
        // 获取角色
        saBaseLoginUser.setRoles(roleList);
        // 获取表单显示字段
        saBaseLoginUser.setDisplayColumnMap(bizTabularDisplayApi.getDisplayColumnMap(saBaseLoginUser.getId()));
        // 缓存用户信息，此处使用TokenSession为了指定时间内无操作则自动下线
        StpUtil.getTokenSession().set("loginUser", saBaseLoginUser);
        // 返回token
        return StpUtil.getTokenInfo().tokenValue;
    }
}
