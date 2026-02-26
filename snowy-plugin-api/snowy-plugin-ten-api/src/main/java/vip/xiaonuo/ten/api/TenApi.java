
package vip.xiaonuo.ten.api;

/**
 * 多租户API接口
 *
 * @author xuyuxiang
 * @date 2022/3/10 18:54
 **/
public interface TenApi {

    /**
     * 获取是否开启多租户
     *
     * @author xuyuxiang
     * @date 2022/7/19 18:34
     **/
    boolean getTenEnabled();

    /**
     * 获取租户id隔离的租户模式下，租户字段名称
     *
     * @author xuyuxiang
     * @date 2022/7/20 18:10
     **/
    String getDefaultTenColumnName();

    /**
     * 获取默认租户id
     *
     * @author xuyuxiang
     * @date 2022/7/19 19:30
     **/
    String getDefaultTenId();

    /**
     * 获取当前的租户id
     *
     * @author xuyuxiang
     * @date 2022/6/14 15:04
     **/
    String getCurrentTenId();

    /**
     * 获取当前的租户域名
     *
     * @author xuyuxiang
     * @date 2022/6/14 15:04
     **/
    String getCurrentTenDomain();
}
