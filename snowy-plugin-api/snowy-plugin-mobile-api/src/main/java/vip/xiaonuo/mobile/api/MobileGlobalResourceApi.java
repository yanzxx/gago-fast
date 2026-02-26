
package vip.xiaonuo.mobile.api;

import cn.hutool.json.JSONObject;

import java.util.List;

/**
 * 移动端按钮API
 *
 * @author xuyuxiang
 * @date 2023/2/1 9:52
 **/
public interface MobileGlobalResourceApi {
    /**
     * 获取移动端全局权限授权数
     *
     * @return
     * @author wanglin
     * @date 2023/10/26 20:00
     */
    List<JSONObject> mobileGlobalResourceSelector();

    /**
     * 根据id集合获取全局权限码列表
     *
     * @return
     * @author 每天一点
     * @date 2023/2/5 13:26
     */
    List<String> getCodeListByIds(List<String> mobileGlobalResourceIds);
}
