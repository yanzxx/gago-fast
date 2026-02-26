
package vip.xiaonuo.dbs.modular.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.springframework.stereotype.Service;
import vip.xiaonuo.dbs.api.DbsApi;
import vip.xiaonuo.dbs.modular.param.DbsStorageIdParam;
import vip.xiaonuo.dbs.modular.param.DbsStorageTableColumnParam;
import vip.xiaonuo.dbs.modular.result.DbsTableColumnResult;
import vip.xiaonuo.dbs.modular.service.DbsService;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据源API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/3/8 16:34
 **/
@Service
public class DbsApiProvider implements DbsApi {

    @Resource
    private DataSource dataSource;

    @Resource
    private DbsService dbsStorageService;

    @Override
    public String getDefaultDataSourceName() {
        return new DynamicDataSourceProperties().getPrimary();
    }

    @Override
    public String getCurrentDataSourceName() {
        return dbsStorageService.getCurrentDataSourceName();
    }

    @Override
    public DataSource getCurrentDataSource() {
        return dbsStorageService.getCurrentDataSource();
    }

    @Override
    public void changeDataSource(String name) {
        dbsStorageService.changeDataSource(name);
    }

    @Override
    public JSONObject dbsDetail(String dbsId) {
        DbsStorageIdParam dbsStorageIdParam = new DbsStorageIdParam();
        dbsStorageIdParam.setId(dbsId);
        return JSONUtil.parseObj(dbsStorageService.detail(dbsStorageIdParam));
    }

    @Override
    public List<JSONObject> dbsSelector() {
        return dbsStorageService.dbsSelector();
    }

    @Override
    public List<JSONObject> tenDbsSelector() {
        return dbsStorageService.tenDbsSelector();
    }

    @Override
    public List<String> tableColumns(String tableName) {
        DbsStorageTableColumnParam dbsStorageTableColumnParam = new DbsStorageTableColumnParam();
        dbsStorageTableColumnParam.setTableName(tableName);
        return dbsStorageService.tableColumns(dbsStorageTableColumnParam).stream().map(DbsTableColumnResult::getColumnName)
                .collect(Collectors.toList());
    }
}
