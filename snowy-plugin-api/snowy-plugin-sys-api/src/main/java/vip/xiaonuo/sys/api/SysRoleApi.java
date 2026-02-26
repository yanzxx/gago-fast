
package vip.xiaonuo.sys.api;

import cn.hutool.json.JSONObject;

import java.util.List;

/**
 * 角色API
 *
 * @author xuyuxiang
 * @date 2022/6/6 11:36
 **/
public interface SysRoleApi {

    /**
     * 判断组织下是否存在角色
     *
     * @author xuyuxiang
     * @date 2022/8/2 11:16
     */
    boolean orgHasRole(List<String> orgIdList);

    /**
     * 获取角色选择器
     *
     * @author xuyuxiang
     * @date 2022/7/22 14:49
     **/
    List<JSONObject> roleSelector(String orgId, String category, String searchKey);

    /**
     * 代码生成菜单按钮授权
     *
     * @author xuyuxiang
     * @date 2022/11/1 15:58
     **/
    void grantForGenMenuAndButton(String menuId);
}
