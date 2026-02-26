
package vip.xiaonuo.dbs.api;

import cn.hutool.json.JSONObject;

import javax.sql.DataSource;
import java.util.List;

/**
 * 数据源API接口
 *
 * @author xuyuxiang
 * @date 2022/3/8 16:30
 **/
public interface DbsApi {

    /**
     * 获取默认的数据源名称
     *
     * @author xuyuxiang
     * @date 2022/3/11 14:25
     **/
    String getDefaultDataSourceName();

    /**
     * 获取当前正在使用的数据源名称
     *
     * @author xuyuxiang
     * @date 2022/3/8 16:31
     **/
    String getCurrentDataSourceName();

    /**
     * 获取当前正在使用的数据源
     *
     * @author xuyuxiang
     * @date 2022/3/8 16:31
     **/
    DataSource getCurrentDataSource();

    /**
     * 切换数据源
     *
     * @param name 数据源名称
     * @author xuyuxiang
     * @date 2022/3/8 16:31
     **/
    void changeDataSource(String name);

    /**
     * 获取数据源详情
     *
     * @author yubaoshan
     * @date 2022/7/11 17:53
     */
    JSONObject dbsDetail(String dbsId);

    /**
     * 获取全部数据源列表
     *
     * @author yubaoshan
     * @date 2022/7/11 17:53
     */
    List<JSONObject> dbsSelector();

    /**
     * 获取租户数据源列表，只查询租户类型数据源
     *
     * @author yubaoshan
     * @date 2022/7/11 17:53
     */
    List<JSONObject> tenDbsSelector();

    /**
     * 根据表名称获取字段名称列表
     *
     * @author xuyuxiang
     * @date 2022/7/19 18:47
     **/
    List<String> tableColumns(String tableName);
}
