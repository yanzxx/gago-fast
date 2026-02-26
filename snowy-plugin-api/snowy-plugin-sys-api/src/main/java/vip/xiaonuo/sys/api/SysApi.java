
package vip.xiaonuo.sys.api;

/**
 * 系统模块综合API
 *
 * @author xuyuxiang
 * @date 2022/9/26 14:24
 **/
public interface SysApi {

    /**
     * 初始化ID类型的租户系统模块数据
     *
     * @author xuyuxiang
     * @date 2022/9/26 14:25
     **/
    void initTenDataForCategoryId(String tenId, String tenName);

    /**
     * 删除ID类型的租户系统模块数据
     *
     * @author xuyuxiang
     * @date 2022/9/26 14:25
     **/
    void removeTenDataForCategoryId(String tenId);
}
