
package vip.xiaonuo.auth.core.util;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import vip.xiaonuo.auth.api.SaBaseLoginUserApi;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.common.util.CommonServletUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * B端登录用户工具类
 *
 * @author xuyuxiang
 * @date 2022/7/8 10:40
 **/
public class StpLoginUserUtil {

    /**
     * 获取当前B端登录用户
     *
     * @author xuyuxiang
     * @date 2022/7/8 10:41
     **/
    public static SaBaseLoginUser getLoginUser() {
        SaBaseLoginUser loginUser = (SaBaseLoginUser) StpUtil.getTokenSession().get("loginUser");
        if (ObjectUtil.isNotEmpty(loginUser)) {
            return loginUser;
        }
        // 兜底：token session未命中时按loginId回源，避免因会话缓存异常导致全链路NPE
        SaBaseLoginUserApi loginUserApi = SpringUtil.getBean("loginUserApi", SaBaseLoginUserApi.class);
        loginUser = loginUserApi.getUserById(StpUtil.getLoginIdAsString());
        if (ObjectUtil.isNotEmpty(loginUser)) {
            try {
                StpUtil.getTokenSession().set("loginUser", loginUser);
            } catch (Exception ignored) {
                // 忽略缓存异常，至少保证当前请求可用
            }
        }
        return loginUser;
    }

    public static List<String> getRoleIds() {
        return StpLoginUserUtil.getLoginUser().getRoles().stream().
                map(jsonObject -> jsonObject.getStr("id")).collect(Collectors.toList());
    }

    public static String getUserId() {
        return StpLoginUserUtil.getLoginUser().getId();
    }

    public static String getDeptId() {
        return StpLoginUserUtil.getLoginUser().getOrgId();
    }

    /**
     * 获取当前B端登录用户的当前请求接口的数据范围
     *
     * @author xuyuxiang
     * @date 2022/7/8 10:41
     **/
    public static List<String> getLoginUserDataScope() {
        List<String> resultList = CollectionUtil.newArrayList();
        SaBaseLoginUser loginUser = getLoginUser();
        if (ObjectUtil.isNotEmpty(loginUser) && ObjectUtil.isNotEmpty(loginUser.getDataScopeList())
                && ObjectUtil.isNotEmpty(CommonServletUtil.getRequest())) {
            String apiUrl = CommonServletUtil.getRequest().getServletPath();
            loginUser.getDataScopeList().forEach(dataScope -> {
                if (ObjectUtil.isNotEmpty(dataScope) && apiUrl.equals(dataScope.getApiUrl())
                        && ObjectUtil.isNotEmpty(dataScope.getDataScope())) {
                    resultList.addAll(dataScope.getDataScope());
                }
            });
        }
        // Fallback: avoid empty scope caused by stale token-session cache or missing scope initialization.
        // Use current login org as minimal available data scope.
        if (CollectionUtil.isEmpty(resultList)) {
            String orgId = ObjectUtil.isNotEmpty(loginUser) ? loginUser.getOrgId() : null;
            if (ObjectUtil.isNotEmpty(orgId)) {
                resultList.add(orgId);
            }
        }
        return resultList;
    }


}
