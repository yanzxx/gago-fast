
package vip.xiaonuo.sys.core.listener;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.common.listener.CommonDataChangeListener;
import vip.xiaonuo.sys.core.enums.SysDataTypeEnum;

import java.util.List;

/**
 * 系统模块数据变化侦听器
 *
 * @author xuyuxiang
 * @date 2023/3/3 10:44
 **/
@Component
public class SysOrgDataChangeListener implements CommonDataChangeListener {

    @Override
    public boolean isSupportDataType(String dataType) {
        return dataType.equals(SysDataTypeEnum.ORG.getValue());
    }

    @Override
    public void doAddWithDataIdList(String dataType, List<String> dataIdList, JSONObject otherDate) {
        // 如果检测到机构增加，则将该机构加入到当前登录用户的数据范围缓存
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        saBaseLoginUser.getDataScopeList().forEach(dataScope -> dataScope.getDataScope().addAll(dataIdList));
        saBaseLoginUser.setDataScopeList(saBaseLoginUser.getDataScopeList());
        // 重新缓存当前登录用户信息
        StpUtil.getTokenSession().set("loginUser", saBaseLoginUser);
    }

    @Override
    public void doAddWithDataList(String dataType, JSONArray jsonArray) {
        // 此处可做额外处理
    }

    @Override
    public void doUpdateWithDataIdList(String dataType, List<String> dataIdList, JSONObject otherDate) {
        // 此处可做额外处理
    }

    @Override
    public void doUpdateWithDataList(String dataType, JSONArray jsonArray) {
        // 此处可做额外处理
    }

    @Override
    public void doDeleteWithDataIdList(String dataType, List<String> dataIdList, JSONObject otherDate) {
        // 此处可做额外处理
    }
}
