
package vip.xiaonuo.dbs.modular.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dbs.modular.entity.DbsStorage;
import vip.xiaonuo.dbs.modular.param.*;
import vip.xiaonuo.dbs.modular.result.DbsTableColumnResult;
import vip.xiaonuo.dbs.modular.result.DbsTableResult;

import javax.sql.DataSource;
import java.util.List;

/**
 * 数据源Service接口
 *
 * @author xuyuxiang
 * @date 2022/6/8 19:40
 **/
public interface DbsService extends IService<DbsStorage> {

    /**
     * 获取数据源分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<DbsStorage> page(DbsStoragePageParam dbsStoragePageParam);

    /**
     * 添加数据源
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(DbsStorageAddParam dbsStorageAddParam);

    /**
     * 编辑数据源
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(DbsStorageEditParam dbsStorageEditParam);

    /**
     * 删除数据源
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<DbsStorageIdParam> dbsStorageIdParamList);

    /**
     * 获取数据源详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DbsStorage detail(DbsStorageIdParam dbsStorageIdParam);

    /**
     * 获取数据源详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DbsStorage queryEntity(String id);

    /* ====数据源部分所需要用到的选择器==== */

    /**
     * 获取数据库中所有表
     *
     * 返回结果：tableName：表名称，tableRemark：表注释
     *
     * @author xuyuxiang
     * @date 2022/6/8 19:46
     **/
    List<DbsTableResult> tables();

    /**
     * 获取数据库表中所有字段
     *
     * 返回结果：columnName：字段名称，columnRemark：字段注释
     *
     * @author xuyuxiang
     * @date 2022/6/8 19:46
     **/
    List<DbsTableColumnResult> tableColumns(DbsStorageTableColumnParam dbsStorageTableColumnParam);

    /**
     * 获取全部数据源列表
     *
     * @author xuyuxiang
     * @date 2022/7/19 18:49
     **/
    List<JSONObject> dbsSelector();

    /**
     * 获取数据源列表，只查询租户类型数据源
     *
     * @author xuyuxiang
     * @date 2022/7/19 18:49
     **/
    List<JSONObject> tenDbsSelector();

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
}
