
package vip.xiaonuo.dev.api;

/**
 * 开发工具模块综合API
 *
 * @author xuyuxiang
 * @date 2022/9/26 14:24
 **/
public interface DevApi {

    /**
     * 初始化ID类型的租户开发工具模块数据
     *
     * @author xuyuxiang
     * @date 2022/9/26 14:25
     **/
    void initTenDataForCategoryId(String tenId, String tenName);

    /**
     * 删除ID类型的租户开发工具模块数据
     *
     * @author xuyuxiang
     * @date 2022/9/26 14:25
     **/
    void removeTenDataForCategoryId(String tenId);
}
