
package vip.xiaonuo.gen.modular.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.util.CommonCryptogramUtil;
import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;
import vip.xiaonuo.dbs.api.DbsApi;
import vip.xiaonuo.gen.core.util.GenDbTypeUtil;
import vip.xiaonuo.gen.modular.basic.entity.GenBasic;
import vip.xiaonuo.gen.modular.basic.enums.GenDbsIdEnum;
import vip.xiaonuo.gen.modular.basic.enums.GenEffectTypeEnum;
import vip.xiaonuo.gen.modular.basic.enums.GenYesNoEnum;
import vip.xiaonuo.gen.modular.basic.mapper.GenBasicMapper;
import vip.xiaonuo.gen.modular.basic.param.*;
import vip.xiaonuo.gen.modular.basic.result.GenBasicDbsSelectorResult;
import vip.xiaonuo.gen.modular.basic.result.GenBasicPreviewResult;
import vip.xiaonuo.gen.modular.basic.result.GenBasicTableColumnResult;
import vip.xiaonuo.gen.modular.basic.result.GenBasicTableResult;
import vip.xiaonuo.gen.modular.basic.service.GenBasicService;
import vip.xiaonuo.gen.modular.config.entity.GenConfig;
import vip.xiaonuo.gen.modular.config.param.GenConfigAddParam;
import vip.xiaonuo.gen.modular.config.service.GenConfigService;
import vip.xiaonuo.gen.prop.GenProperties;
import vip.xiaonuo.sys.api.SysButtonApi;
import vip.xiaonuo.sys.api.SysMenuApi;
import vip.xiaonuo.sys.api.SysRoleApi;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 代码生成基础Service接口实现类
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Service
public class GenBasicServiceImpl extends ServiceImpl<GenBasicMapper, GenBasic> implements GenBasicService {

    private static final String DB_URL_KEY = "spring.datasource.dynamic.datasource.master.url";

    private static final String DB_USERNAME_KEY = "spring.datasource.dynamic.datasource.master.username";

    private static final String DB_PASSWORD_KEY = "spring.datasource.dynamic.datasource.master.password";

    @Resource
    private GenProperties genProperties;

    private static final List<JSONObject> GEN_SQL_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "Mysql.sql.btl"),
            JSONUtil.createObj().set("name", "PostgreSQL.sql.btl"));

    private static final List<JSONObject> GEN_FRONT_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "Api.js.btl").set("path", "api"),
            JSONUtil.createObj().set("name", "Form.vue.btl").set("path",  "views"),
            JSONUtil.createObj().set("name", "Index.vue.btl").set("path",  "views"));

    private static final List<JSONObject> GEN_BACKEND_FILE_LIST = CollectionUtil.newArrayList(
            JSONUtil.createObj().set("name", "Controller.java.btl").set("path", "controller"),
            JSONUtil.createObj().set("name", "Entity.java.btl").set("path", "entity"),
            JSONUtil.createObj().set("name", "Enum.java.btl").set("path", "enums"),
            JSONUtil.createObj().set("name", "ExportVO.java.btl").set("path", "entity" + File.separator + "VO"),
            JSONUtil.createObj().set("name", "IntoBO.java.btl").set("path", "entity" + File.separator + "BO"),
            JSONUtil.createObj().set("name", "Mapper.java.btl").set("path", "mapper"),
            JSONUtil.createObj().set("name", "Mapper.xml.btl").set("path", "mapper" + File.separator + "mapping"),
            JSONUtil.createObj().set("name", "AddParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "EditParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "IdParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "PageParam.java.btl").set("path", "param"),
            JSONUtil.createObj().set("name", "Service.java.btl").set("path", "service"),
            JSONUtil.createObj().set("name", "ServiceImpl.java.btl").set("path", "service" + File.separator + "impl"));

    private static final String SORT_CODE_KEY = "SORT_CODE";

    private static final String CREATE_USER_KEY = "CREATE_USER";

    private static final String CREATE_TIME_KEY = "CREATE_TIME";

    private static final String UPDATE_USER_KEY = "UPDATE_USER";

    private static final String UPDATE_TIME_KEY = "UPDATE_TIME";

    private static final String DELETE_FLAG_KEY = "DELETE_FLAG";

    @Resource
    private Environment environment;

    @Resource
    private MybatisPlusProperties mybatisPlusProperties;

    @Resource
    private GenConfigService genConfigService;

    @Resource
    private SysMenuApi sysMenuApi;

    @Resource
    private SysButtonApi sysButtonApi;

    @Resource
    private SysRoleApi sysRoleApi;

    @Resource
    private DbsApi dbsApi;

    @Override
    public Page<GenBasic> page(GenBasicPageParam param) {
        QueryWrapper<GenBasic> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(param.getBusName())) {
            queryWrapper.lambda().like(GenBasic::getBusName, param.getBusName());
        }
        if (StrUtil.isNotBlank(param.getFunctionName())) {
            queryWrapper.lambda().like(GenBasic::getFunctionName, param.getFunctionName());
        }
        if (StrUtil.isNotBlank(param.getClassName())) {
            queryWrapper.lambda().like(GenBasic::getClassName, param.getClassName());
        }
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            CommonSortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(GenBasic::getSortCode).orderByDesc(GenBasic::getCreateTime);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GenBasic add(GenBasicAddParam genBasicAddParam) {
        GenBasic genBasic = BeanUtil.toBean(genBasicAddParam, GenBasic.class);
        this.save(genBasic);
        addGenConfig(genBasic, getGenBasicTableColumnResults(genBasic, genBasicAddParam.getDbsId()));
        return genBasic;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GenBasic edit(GenBasicEditParam genBasicEditParam) {
        GenBasic genBasic = this.queryEntity(genBasicEditParam.getId());
        BeanUtil.copyProperties(genBasicEditParam, genBasic);
        this.updateById(genBasic);

        QueryWrapper<GenConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(GenConfig::getBasicId, genBasic.getId());
        queryWrapper.lambda().eq(GenConfig::getDeleteFlag,"NOT_DELETE");

        List<GenConfig> genConfigs = genConfigService.list(queryWrapper);
        Map<String, GenConfig> genConfigMap = genConfigs.stream().collect(Collectors.toMap(genConfig -> genConfig.getFieldName().toLowerCase(), Function.identity()));
        List<GenBasicTableColumnResult> dbFields = getGenBasicTableColumnResults(genBasic, genBasicEditParam.getDbsId());

        List<GenBasicTableColumnResult> addList = new ArrayList<>();
        List<GenConfig> updateList = new ArrayList<>();
        for (GenBasicTableColumnResult dbField : dbFields) {
            String columnName = dbField.getColumnName().toLowerCase();
            GenConfig genConfig = genConfigMap.remove(columnName);
            if (genConfig == null){
                // 数据库的字段不在原来的配置项里，新增配置项
                addList.add(dbField);
                continue;
            }

            if (!genConfig.getFieldType().equals(dbField.getTypeName()) || !(genConfig.getFieldRemark().equals(dbField.getColumnRemark()))) {
                genConfig.setFieldType(dbField.getTypeName());
                genConfig.setFieldRemark(dbField.getColumnRemark());
                genConfig.setFieldJavaType(GenDbTypeUtil.getJavaTypeBySqlType(dbField.getTypeName()));
                updateList.add(genConfig);
            }
        }


        if (CollectionUtil.isNotEmpty(addList)){
            addGenConfig(genBasic, addList);
        }

        if (CollectionUtil.isNotEmpty(updateList)){
            genConfigService.updateBatchById(updateList);
        }

        if (CollectionUtil.isNotEmpty(genConfigMap)){
            genConfigService.removeBatchByIds(genConfigMap.values());
        }

        return genBasic;
    }

    //获取数据
    private List<GenBasicTableColumnResult> getGenBasicTableColumnResults(GenBasic genBasic, String dbsId) {
        GenBasicTableColumnParam tableColumnParam = new GenBasicTableColumnParam();
        tableColumnParam.setTableName(genBasic.getDbTable());
        List<GenBasicTableColumnResult> resultList;
        if (dbsId.equals(GenDbsIdEnum.MASTER.getValue())) {
            resultList = this.tableColumns(tableColumnParam);
        } else {
            GenBasicDbsTableColumnParam genBasicDbsTableColumnParam = new GenBasicDbsTableColumnParam();
            genBasicDbsTableColumnParam.setDbsId(dbsId);
            genBasicDbsTableColumnParam.setTableName(genBasic.getDbTable());
            resultList = this.tableColumnsByDbsId(genBasicDbsTableColumnParam);
        }
        return resultList;
    }

    @Override
    public JSONObject getGenFileNames() {
        List<String> genSqlFileNames = GEN_SQL_FILE_LIST.stream().map(jsonObject -> StrUtil.removeSuffix(jsonObject.getStr("name"), ".btl")).collect(Collectors.toList());
        List<String> genFrontFileNames = GEN_FRONT_FILE_LIST.stream().map(jsonObject -> StrUtil.removeSuffix(jsonObject.getStr("name"), ".btl")).collect(Collectors.toList());
        List<String> genBackendFileNames = GEN_BACKEND_FILE_LIST.stream().map(jsonObject -> StrUtil.removeSuffix(jsonObject.getStr("name"), ".btl")).collect(Collectors.toList());

        return JSONUtil.createObj()
                .set("genSqlFileNames", genSqlFileNames)
                .set("genFrontFileNames", genFrontFileNames)
                .set("genBackendFileNames", genBackendFileNames);
    }

    /**
     * 新增表字段至配置内
     *
     * @author yubaoshan
     * @date 2023/02/22 00:54
     */
    private void addGenConfig (GenBasic genBasic, List<GenBasicTableColumnResult> resultList) {
        List<GenConfig> list = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            GenBasicTableColumnResult item = resultList.get(i);
            GenConfigAddParam addParam = new GenConfigAddParam();
            addParam.setBasicId(genBasic.getId());
            if (item.getColumnName().equals(genBasic.getDbTableKey())) {
                addParam.setIsTableKey(GenYesNoEnum.Y.getValue());
            } else {
                addParam.setIsTableKey(GenYesNoEnum.N.getValue());
            }
            addParam.setFieldName(item.getColumnName());
            addParam.setFieldType(item.getTypeName());
            addParam.setFieldRemark(item.getColumnRemark());
            addParam.setFieldJavaType(GenDbTypeUtil.getJavaTypeBySqlType(item.getTypeName()));
            addParam.setEffectType(GenEffectTypeEnum.INPUT.getValue().toLowerCase());
            // 除主键、删除标志、创建人、创建时间、修改人和修改时间外，所有默认在列表显示、在增改显示、非列省略、非必填，非查询
            String logicDeleteField = mybatisPlusProperties.getGlobalConfig().getDbConfig().getLogicDeleteField();
            if(ObjectUtil.isEmpty(logicDeleteField)) {
                logicDeleteField = "DELETE_FLAG";
            }
            if(genBasic.getDbTableKey().equalsIgnoreCase(item.getColumnName()) ||
                    logicDeleteField.equalsIgnoreCase(item.getColumnName()) ||
                    CREATE_USER_KEY.equalsIgnoreCase(item.getColumnName()) ||
                    CREATE_TIME_KEY.equalsIgnoreCase(item.getColumnName()) ||
                    UPDATE_USER_KEY.equalsIgnoreCase(item.getColumnName()) ||
                    UPDATE_TIME_KEY.equalsIgnoreCase(item.getColumnName())) {
                addParam.setWhetherTable(GenYesNoEnum.N.getValue());
                addParam.setWhetherAddUpdate(GenYesNoEnum.N.getValue());
            } else {
                addParam.setWhetherTable(GenYesNoEnum.Y.getValue());
                addParam.setWhetherAddUpdate(GenYesNoEnum.Y.getValue());
            }
            addParam.setWhetherRetract(GenYesNoEnum.N.getValue());
            addParam.setWhetherSort(GenYesNoEnum.N.getValue());
            addParam.setWhetherRequired(GenYesNoEnum.N.getValue());
            addParam.setQueryWhether(GenYesNoEnum.N.getValue());
            addParam.setSortCode(i);
            list.add(BeanUtil.toBean(addParam, GenConfig.class));
        }
        if (CollectionUtil.isNotEmpty(list)){
            genConfigService.saveBatch(list);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<GenBasicIdParam> genBasicIdParamList) {
        List<String> basicIdIdList = CollStreamUtil.toList(genBasicIdParamList, GenBasicIdParam::getId);
        if(ObjectUtil.isNotEmpty(basicIdIdList)) {
            // 级联删除配置
            genConfigService.remove(new LambdaQueryWrapper<GenConfig>().in(GenConfig::getBasicId, basicIdIdList));
            // 执行删除
            this.removeByIds(basicIdIdList);
        }
    }

    @Override
    public GenBasic detail(GenBasicIdParam genBasicIdParam) {
        return this.queryEntity(genBasicIdParam.getId());
    }

    @Override
    public GenBasic queryEntity(String id) {
        GenBasic genBasic = this.getById(id);
        if(ObjectUtil.isEmpty(genBasic)) {
            throw new CommonException("代码生成基础不存在，id值为：{}", id);
        }
        return genBasic;
    }

    @Override
    public List<GenBasicDbsSelectorResult> dbsSelector() {
        return dbsApi.dbsSelector().stream()
                .map(jsonObject -> JSONUtil.toBean(jsonObject, GenBasicDbsSelectorResult.class)).collect(Collectors.toList());
    }

    @Override
    public List<GenBasicTableResult> tablesByDbsId(GenBasicDbsTableParam genBasicDbsTableParam) {
        JSONObject jsonObject = dbsApi.dbsDetail(genBasicDbsTableParam.getDbsId());
        String poolName = jsonObject.getStr("poolName");
        String url = jsonObject.getStr("url");
        String userName = jsonObject.getStr("username");
        String password = CommonCryptogramUtil.doSm2Decrypt(jsonObject.getStr("password"));
        if(ObjectUtil.hasEmpty(url, userName, password)) {
            throw new CommonException("数据源{}配置信息不完整", poolName);
        }
        return queryTables(url, userName, password);
    }

    @Override
    public List<GenBasicTableColumnResult> tableColumnsByDbsId(GenBasicDbsTableColumnParam dbsTableColumnParam) {
        JSONObject jsonObject = dbsApi.dbsDetail(dbsTableColumnParam.getDbsId());
        String poolName = jsonObject.getStr("poolName");
        String url = jsonObject.getStr("url");
        String userName = jsonObject.getStr("username");
        String password = CommonCryptogramUtil.doSm2Decrypt(jsonObject.getStr("password"));
        if(ObjectUtil.hasEmpty(url, userName, password)) {
            throw new CommonException("数据源{}配置信息不完整", poolName);
        }
        return queryTableColumns(url, userName, password, dbsTableColumnParam.getTableName());
    }

    @Override
    public List<GenBasicTableResult> tables() {
        String url = environment.getProperty(DB_URL_KEY);
        String userName = environment.getProperty(DB_USERNAME_KEY);
        String password = environment.getProperty(DB_PASSWORD_KEY);
        if(ObjectUtil.hasEmpty(url, userName, password)) {
            throw new CommonException("当前数据源配置信息不完整");
        }
        return this.queryTables(url, userName, password);
    }

    @Override
    public List<GenBasicTableColumnResult> tableColumns(GenBasicTableColumnParam genBasicTableColumnParam) {
        String url = environment.getProperty(DB_URL_KEY);
        String userName = environment.getProperty(DB_USERNAME_KEY);
        String password = environment.getProperty(DB_PASSWORD_KEY);
        if(ObjectUtil.hasEmpty(url, userName, password)) {
            throw new CommonException("当前数据源配置信息不完整");
        }
        return this.queryTableColumns(url, userName, password, genBasicTableColumnParam.getTableName());
    }

    /**
     * 查询指定数据源中的所有表
     *
     * @author xuyuxiang
     * @date 2023/2/1 10:31
     **/
    private List<GenBasicTableResult> queryTables(String url, String userName, String password) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, userName, password);
            DatabaseMetaData metaData = conn.getMetaData();
            String schema = null;
            if (metaData.getURL().toLowerCase().contains("jdbc:oracle")) {
                schema = metaData.getUserName();
            }
            List<GenBasicTableResult> tables = new ArrayList<>();
            rs = metaData.getTables(null, schema, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!StrUtil.startWithIgnoreCase(tableName, "ACT_")) {
                    GenBasicTableResult genBasicTableResult = new GenBasicTableResult();
                    genBasicTableResult.setTableName(tableName);
                    String remarks = rs.getString("REMARKS");
                    if(ObjectUtil.isEmpty(remarks)) {
                        genBasicTableResult.setTableRemark(tableName);
                    } else {
                        genBasicTableResult.setTableRemark(remarks);
                    }
                    tables.add(genBasicTableResult);
                }
            }
            return tables;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonException("获取数据库表失败");
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
        }
    }

    /**
     * 查询指定数据源中指定表的所有字段
     *
     * @author xuyuxiang
     * @date 2023/2/1 11:09
     **/
    private List<GenBasicTableColumnResult> queryTableColumns(String url, String userName, String password, String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, userName, password);
            DatabaseMetaData metaData = conn.getMetaData();
            String schema = null;
            if (metaData.getURL().toLowerCase().contains("jdbc:oracle")) {
                schema = metaData.getUserName();
            }
            List<GenBasicTableColumnResult> columns = new ArrayList<>();
            rs = metaData.getColumns(null, schema, tableName, "%");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME").toUpperCase();
                GenBasicTableColumnResult genBasicTableColumnResult = new GenBasicTableColumnResult();
                genBasicTableColumnResult.setColumnName(columnName);
                String remarks = rs.getString("REMARKS");
                if(ObjectUtil.isEmpty(remarks)) {
                    genBasicTableColumnResult.setColumnRemark(columnName);
                } else {
                    genBasicTableColumnResult.setColumnRemark(remarks);
                }
                String typeName = rs.getString("TYPE_NAME").toUpperCase();
                // 获取字段的数据类型信息
                rs.getInt("DATA_TYPE");
                rs.getInt("COLUMN_SIZE");
                rs.getInt("DECIMAL_DIGITS");

                if(ObjectUtil.isEmpty(typeName)) {
                    genBasicTableColumnResult.setTypeName("NONE");
                } else {
                    // 处理PostgreSQL特殊类型
                    if(metaData.getURL().toLowerCase().contains("jdbc:postgresql")) {
                        // 处理PostgreSQL的serial类型，它在元数据中显示为int4
                        if(typeName.equals("INT4") && columnName.equals(tableName + "_id")) {
                            typeName = "SERIAL";
                        }
                        // 处理PostgreSQL的bigserial类型，它在元数据中显示为int8
                        if(typeName.equals("INT8") && columnName.equals(tableName + "_id")) {
                            typeName = "BIGSERIAL";
                        }
                    }
                    genBasicTableColumnResult.setTypeName(typeName);
                }
                columns.add(genBasicTableColumnResult);
            }
            return columns;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonException("获取数据库表字段失败，表名称：{}", tableName);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execGenZip(GenBasicFileParam genBasicFileParam, HttpServletResponse response) throws IOException {
        try {
            File tempFolder = this.genTempFolder(genBasicFileParam, response, true);
            if(tempFolder == null) {
                CommonResponseUtil.renderError(response, "代码生成基础不存在，id值为：" + genBasicFileParam.getId());
                return;
            }
            // 压缩
            File zip = ZipUtil.zip(tempFolder);
            // 压缩完毕删除临时目录
            FileUtil.del(tempFolder);
            // 下载
            CommonDownloadUtil.download(zip, response);
        } catch (Exception e) {
            CommonResponseUtil.renderError(response, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execGenPro(GenBasicFileParam genBasicFileParam, HttpServletResponse response) throws IOException {
        File tempFolder = this.genTempFolder(genBasicFileParam, response, false);
        // 定义前端生成的目录
        String genProjectFrontendPath = System.getProperty("user.dir") + File.separator + genProperties.getFrontBaseDir() + File.separator + "src";

        if(!FileUtil.exist(genProjectFrontendPath)) {
            throw new CommonException("前端代码生成位置：{}不存在，请检查位置", genProjectFrontendPath);
        }

        GenBasic genBasic = this.queryEntity(genBasicFileParam.getId());
        String genModuleName = genBasic.getModuleName();
        String genPluginName = genBasic.getPluginName();
        String genProjectPluginBizKey = genProperties.getBackendBaseDir() + File.separator + genPluginName;

        // 定义后端生成的目录
        String genProjectBackendPath = System.getProperty("user.dir") + File.separator + genProjectPluginBizKey + File.separator + "src" +
                File.separator + "main" + File.separator + "java";

        if(!FileUtil.exist(genProjectBackendPath)) {
            throw new CommonException("后端代码生成位置：{}不存在，请检查位置", genProjectBackendPath);
        }
        try {

            // 生成菜单 同时 删除老菜单（包括其下面的菜单、按钮，清除对应的角色与资源信息）
            String classNameLowerFirst = StrUtil.lowerFirst(genBasic.getClassName());
            String menuId = sysMenuApi.addForGenMenu(genBasic.getMenuPid(), classNameLowerFirst, genBasic.getModule(), genBasic.getFunctionName(),
                    StrUtil.SLASH + genModuleName + StrUtil.SLASH + genBasic.getBusName() + StrUtil.SLASH + classNameLowerFirst);

            // 生成按钮
            sysButtonApi.addForGenButton(menuId, genBasic.getClassName(), genBasic.getFunctionName());

            // 授权菜单
            sysRoleApi.grantForGenMenuAndButton(menuId);

            //前端代码移动到前端
            if (CollectionUtil.isNotEmpty(genBasicFileParam.getGenFrontFileNames())){
                FileUtil.moveContent(FileUtil.file(tempFolder + File.separator + "frontend"), FileUtil.file(genProjectFrontendPath), true);
            }

            // 后端代码移动到后端
            if (CollectionUtil.isNotEmpty(genBasicFileParam.getGenBackendFileNames())){
                FileUtil.moveContent(FileUtil.file(tempFolder + File.separator + "backend"), FileUtil.file(genProjectBackendPath), true);
            }

            // 移动完毕删除临时目录
            FileUtil.del(tempFolder);
        } catch (Exception e) {
            log.error(">>> 代码生成异常：", e);
            throw new CommonException("代码生成异常");
        }
    }

    /**
     * 获取临时目录
     *
     * @author xuyuxiang yubaoshan
     * @date 2022/10/28 21:36
     */
    private File genTempFolder(GenBasicFileParam genBasicFileParam, HttpServletResponse response, boolean isZip) throws IOException {
        GenBasic genBasic = this.getById(genBasicFileParam.getId());
        if(ObjectUtil.isEmpty(genBasic)) {
            // 如果是压缩包下载应该使用CommonResponseUtil渲染异常
            if(isZip) {
                return null;
            } else {
                // 否则可以直接抛出异常
                throw new CommonException("代码生成基础不存在，id值为：{}", genBasicFileParam.getId());
            }
        }
        GenBasicPreviewResult genBasicPreviewResult = this.previewGen(genBasicFileParam);
        // 先删除压缩包
        FileUtil.del(FileUtil.getTmpDirPath() + File.separator + genBasic.getFunctionName() + ".zip");
        // 生成临时目录
        File tempFolder = FileUtil.file(FileUtil.getTmpDirPath() + File.separator + genBasic.getFunctionName());
        // 生成SQL代码到临时目录
        genBasicPreviewResult.getGenBasicCodeSqlResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator +
                        genBasicCodeResult.getCodeFileWithPathName())));
        // 生成前端代码到临时目录
        genBasicPreviewResult.getGenBasicCodeFrontendResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator
                        + "frontend" + File.separator + genBasicCodeResult.getCodeFileWithPathName())));
        // 生成后端代码到临时目录
        genBasicPreviewResult.getGenBasicCodeBackendResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator
                        + "backend" + File.separator + genBasicCodeResult.getCodeFileWithPathName())));
        return tempFolder;
    }

    @Override
    public GenBasicPreviewResult previewGen(GenBasicFileParam genBasicFileParam) {
        List<String> genSqlFileNames = genBasicFileParam.getGenSqlFileNames();
        List<String> genFrontFileNames = genBasicFileParam.getGenFrontFileNames();
        List<String> genBackendFileNames = genBasicFileParam.getGenBackendFileNames();

        GenBasic genBasic = this.queryEntity(genBasicFileParam.getId());
        JSONObject bindingJsonObject = this.getBindingJsonObject(genBasic);
        GenBasicPreviewResult genBasicPreviewResult = new GenBasicPreviewResult();


        try {
            // SQL基础路径
            String genSqlBasicPath = "sql";
            // SQL
            GroupTemplate groupTemplateSql = new GroupTemplate(new ClasspathResourceLoader("sqlend"),
                    Configuration.defaultConfiguration());
            List<GenBasicPreviewResult.GenBasicCodeResult> genBasicCodeSqlResultList = CollectionUtil.newArrayList();
            GEN_SQL_FILE_LIST.stream()
                    .filter(jsonObject -> CollectionUtil.isNotEmpty(genSqlFileNames) && genSqlFileNames.contains(StrUtil.removeSuffix(jsonObject.getStr("name"), ".btl")))
                    .forEach(fileJsonObject -> {
                        String fileTemplateName = fileJsonObject.getStr("name");
                        GenBasicPreviewResult.GenBasicCodeResult genBasicCodeSqlResult = new GenBasicPreviewResult.GenBasicCodeResult();
                        Template templateSql = groupTemplateSql.getTemplate(fileTemplateName);
                        templateSql.binding(bindingJsonObject);
                        String resultName = StrUtil.removeSuffix(fileTemplateName, ".btl");
                        genBasicCodeSqlResult.setCodeFileName(resultName);
                        genBasicCodeSqlResult.setCodeFileWithPathName(genSqlBasicPath + File.separator + resultName);
                        genBasicCodeSqlResult.setCodeFileContent(templateSql.render());
                        genBasicCodeSqlResultList.add(genBasicCodeSqlResult);
                    });
            genBasicPreviewResult.setGenBasicCodeSqlResultList(genBasicCodeSqlResultList);

            // 前端基础路径
            String genFrontBasicPath = "";
            // 前端
            GroupTemplate groupTemplateFront = new GroupTemplate(new ClasspathResourceLoader("frontend"),
                    Configuration.defaultConfiguration());
            List<GenBasicPreviewResult.GenBasicCodeResult> genBasicCodeFrontendResultList = CollectionUtil.newArrayList();
            GEN_FRONT_FILE_LIST.stream()
                    .filter(jsonObject -> CollectionUtil.isNotEmpty(genFrontFileNames) && genFrontFileNames.contains(StrUtil.removeSuffix(jsonObject.getStr("name"), ".btl")))
                    .forEach(fileJsonObject -> {
                        String fileTemplateName = fileJsonObject.getStr("name");
                        String fileTemplatePath = fileJsonObject.getStr("path") + File.separator + genBasic.getModuleName();
                        GenBasicPreviewResult.GenBasicCodeResult genBasicCodeFrontResult = new GenBasicPreviewResult.GenBasicCodeResult();
                        Template templateFront = groupTemplateFront.getTemplate(fileTemplateName);
                        templateFront.binding(bindingJsonObject);
                        String resultName = StrUtil.removeSuffix(fileTemplateName, ".btl");
                        resultName = StrUtil.lowerFirst(genBasic.getClassName()) + resultName;
                        if(fileTemplateName.equalsIgnoreCase("Api.js.btl")) {
                            genBasicCodeFrontResult.setCodeFileName(resultName);
                            genBasicCodeFrontResult.setCodeFileWithPathName(genFrontBasicPath + fileTemplatePath + File.separator + resultName);
                        } else {
                            genBasicCodeFrontResult.setCodeFileName(resultName);
                            genBasicCodeFrontResult.setCodeFileWithPathName(genFrontBasicPath + fileTemplatePath + File.separator + genBasic.getBusName() + File.separator + resultName);
                        }
                        genBasicCodeFrontResult.setCodeFileContent(templateFront.render());
                        genBasicCodeFrontendResultList.add(genBasicCodeFrontResult);
                    });
            genBasicPreviewResult.setGenBasicCodeFrontendResultList(genBasicCodeFrontendResultList);

            // 后端基础路径
            String genBackendBasicPath = StrUtil.replace(genBasic.getPackageName(), StrUtil.DOT, File.separator) +
                    File.separator + genBasic.getModuleName() + File.separator + "modular" +  File.separator + genBasic.getBusName() + File.separator;
            // 后端
            GroupTemplate groupTemplateBackEnd = new GroupTemplate(new ClasspathResourceLoader("backend"),
                    Configuration.defaultConfiguration());
            List<GenBasicPreviewResult.GenBasicCodeResult> genBasicCodeBackendResultList = CollectionUtil.newArrayList();
            GEN_BACKEND_FILE_LIST.stream()
                    .filter(jsonObject -> CollectionUtil.isNotEmpty(genBackendFileNames) && genBackendFileNames.contains(StrUtil.removeSuffix(jsonObject.getStr("name"), ".btl")))
                    .forEach(fileJsonObject -> {
                        String fileTemplateName = fileJsonObject.getStr("name");
                        String fileTemplatePath = fileJsonObject.getStr("path");
                        GenBasicPreviewResult.GenBasicCodeResult genBasicCodeBackendResult = new GenBasicPreviewResult.GenBasicCodeResult();
                        Template templateBackend = groupTemplateBackEnd.getTemplate(fileTemplateName);
                        templateBackend.binding(bindingJsonObject);
                        String resultName = StrUtil.removeSuffix(fileTemplateName, ".btl");
                        if(fileTemplateName.equalsIgnoreCase("Entity.java.btl")) {
                            resultName = ".java";
                        }
                        genBasicCodeBackendResult.setCodeFileName(genBasic.getClassName() + resultName);
                        genBasicCodeBackendResult.setCodeFileWithPathName(genBackendBasicPath + fileTemplatePath + File.separator + genBasic.getClassName() + resultName);
                        genBasicCodeBackendResult.setCodeFileContent(templateBackend.render());
                        genBasicCodeBackendResultList.add(genBasicCodeBackendResult);
                    });
            genBasicPreviewResult.setGenBasicCodeBackendResultList(genBasicCodeBackendResultList);
        } catch (Exception e) {
            log.error(">>> 代码生成异常：", e);
            throw new CommonException("代码生成异常");
        }
        return genBasicPreviewResult;
    }

    /**
     * 根据代码生成基础获取构造的参数
     *
     * @author xuyuxiang
     * @date 2022/10/28 21:36
     */
    public JSONObject getBindingJsonObject(GenBasic genBasic) {
        JSONObject bindingJsonObject = JSONUtil.createObj();
        // 是否分库
        bindingJsonObject.set("subDatabase", genBasic.getSubDatabase().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
        // 数据源名称
        bindingJsonObject.set("dbsName", genBasic.getDbsName());
        // 代码模块名
        bindingJsonObject.set("moduleName", genBasic.getModuleName());
        // 功能名
        bindingJsonObject.set("functionName", genBasic.getFunctionName());
        // 业务名
        bindingJsonObject.set("busName", genBasic.getBusName());
        // 包名
        bindingJsonObject.set("packageName", genBasic.getPackageName());
        // 库名
        bindingJsonObject.set("dbTable", genBasic.getDbTable());
        // 类名
        bindingJsonObject.set("className", genBasic.getClassName());
        // 类首字母小写名
        bindingJsonObject.set("classNameFirstLower", StrUtil.lowerFirst(genBasic.getClassName()));
        // 类短横连接名
        bindingJsonObject.set("classNameKebab", NamingCase.toKebabCase(genBasic.getClassName()));
        // 主键名
        bindingJsonObject.set("dbTableKey", genBasic.getDbTableKey());
        // 主键名驼峰
        bindingJsonObject.set("dbTableKeyCamelCase", StrUtil.toCamelCase(genBasic.getDbTableKey().toLowerCase()));
        // 主键首字母大写名
        bindingJsonObject.set("dbTableKeyFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(genBasic.getDbTableKey().toLowerCase())));
        // 主键注释
        bindingJsonObject.set("dbTableKeyRemark", genBasic.getDbTableKey());
        // 表单布局
        bindingJsonObject.set("formLayout", genBasic.getFormLayout());
        // 使用栅格
        bindingJsonObject.set("gridWhether", StrUtil.equalsIgnoreCase(genBasic.getGridWhether(), (GenYesNoEnum.Y.getValue())));
        // 是否继承基础类
        bindingJsonObject.set("extendBase", StrUtil.equalsIgnoreCase(genBasic.getExtendBase(), (GenYesNoEnum.Y.getValue())));
        // 父菜单ID
        bindingJsonObject.set("parentId", genBasic.getMenuPid());
        // 菜单ID
        bindingJsonObject.set("menuId", IdWorker.getIdStr());
        // 菜单编码
        bindingJsonObject.set("menuCode", RandomUtil.randomString(10));
        // 菜单路径
        bindingJsonObject.set("menuPath", StrUtil.SLASH + genBasic.getModuleName() + StrUtil.SLASH + genBasic.getBusName());
        // 菜单组件
        bindingJsonObject.set("menuComponent", genBasic.getModuleName() + StrUtil.SLASH + genBasic.getBusName() + StrUtil.SLASH + "index");
        // 模块ID
        bindingJsonObject.set("moduleId", genBasic.getModule());
        // 添加按钮ID
        bindingJsonObject.set("addButtonId", IdWorker.getIdStr());
        // 编辑按钮ID
        bindingJsonObject.set("editButtonId", IdWorker.getIdStr());
        // 删除按钮ID
        bindingJsonObject.set("deleteButtonId", IdWorker.getIdStr());
        // 批量删除按钮ID
        bindingJsonObject.set("batchDeleteButtonId", IdWorker.getIdStr());
        // 导入按钮ID
        bindingJsonObject.set("excelInButtonId", IdWorker.getIdStr());
        // 导出按钮ID
        bindingJsonObject.set("excelOutButtonId", IdWorker.getIdStr());
        // 作者
        bindingJsonObject.set("authorName", genBasic.getAuthorName());
        // 生成时间
        bindingJsonObject.set("genTime", DateUtil.format(DateTime.now(), " yyyy/MM/dd HH:mm"));
        // 定义配置详情列表
        List<JSONObject> configList = CollectionUtil.newArrayList();
        // 定义是否有排序字段
        AtomicBoolean hasSortCodeField = new AtomicBoolean(false);
        genConfigService.list(new LambdaQueryWrapper<GenConfig>().eq(GenConfig::getBasicId, genBasic.getId()))
                .forEach(genConfig -> {
                    // 定义字段信息
                    JSONObject configItem = JSONUtil.createObj();
                    if(genConfig.getFieldName().equalsIgnoreCase(SORT_CODE_KEY)) {
                        hasSortCodeField.set(true);
                    }

                    // 是否主键
                    boolean isKey = genConfig.getFieldName().equalsIgnoreCase(genBasic.getDbTableKey());
                    if(isKey) {
                        // 如果是主键，则无需作为添加参数，需要作为编辑参数，需要主键注解
                        configItem.set("needAdd", false);
                        configItem.set("needEdit", true);
                        configItem.set("needPage", false);
                        configItem.set("needSort", false);
                        configItem.set("needPageType", "none");
                        configItem.set("required", true);
                        configItem.set("needTableId", true);
                        bindingJsonObject.set("dbTableKeyJavaType", genConfig.getFieldJavaType());
                        bindingJsonObject.set("dbTableKeyRemark", genConfig.getFieldRemark());
                    } else {
                        // 排除删除标志
                        String logicDeleteField = mybatisPlusProperties.getGlobalConfig().getDbConfig().getLogicDeleteField();
                        if(ObjectUtil.isEmpty(logicDeleteField)) {
                            logicDeleteField = "DELETE_FLAG";
                        }
                        if(genConfig.getFieldName().equalsIgnoreCase(logicDeleteField)) {
                            configItem.set("needAdd", false);
                            configItem.set("needEdit", false);
                            configItem.set("needPage", false);
                            configItem.set("needSort",false);
                            configItem.set("needPageType", "none");
                            configItem.set("required", false);
                            configItem.set("needTableId", false);
                        } else {
                            boolean needAddAndUpdate = genConfig.getWhetherAddUpdate().equalsIgnoreCase(GenYesNoEnum.Y.getValue());
                            configItem.set("needAdd", needAddAndUpdate);
                            configItem.set("needEdit", needAddAndUpdate);
                            configItem.set("needPage", genConfig.getQueryWhether().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                            if (StringUtils.isNotEmpty(genConfig.getWhetherSort())){
                                configItem.set("needSort", genConfig.getWhetherSort().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                            }
                            configItem.set("needPageType", genConfig.getQueryType());
                            configItem.set("required", genConfig.getWhetherRequired().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                            configItem.set("needTableId", false);
                        }
                    }
                    // 列显示
                    configItem.set("whetherTable", genConfig.getWhetherTable().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                    // 排序字段（desc）
                    if (StringUtils.isNotEmpty(genConfig.getWhetherSort())){
                        configItem.set("whetherSort", genConfig.getWhetherSort().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                    }
                    // 列省略
                    configItem.set("whetherRetract", genConfig.getWhetherRetract().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                    // 增改
                    configItem.set("whetherAddUpdate", genConfig.getWhetherAddUpdate().equalsIgnoreCase(GenYesNoEnum.Y.getValue()));
                    // 作用类型
                    configItem.set("effectType", genConfig.getEffectType());
                    // 字典值
                    configItem.set("dictTypeCode", genConfig.getDictTypeCode());
                    // 实体类型
                    configItem.set("fieldJavaType", genConfig.getFieldJavaType());
                    // 字段驼峰名
                    configItem.set("fieldNameCamelCase", StrUtil.toCamelCase(genConfig.getFieldName().toLowerCase()));
                    // 字段驼峰首字母大写名
                    configItem.set("fieldNameCamelCaseFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(genConfig.getFieldName().toLowerCase())));
                    // 字段注释
                    configItem.set("fieldRemark", genConfig.getFieldRemark());
                    // 是否需要自动插入
                    configItem.set("needAutoInsert", CREATE_USER_KEY.equalsIgnoreCase(genConfig.getFieldName()) ||
                            CREATE_TIME_KEY.equalsIgnoreCase(genConfig.getFieldName()));
                    // 是否需要自动更新
                    configItem.set("needAutoUpdate", UPDATE_USER_KEY.equalsIgnoreCase(genConfig.getFieldName()) ||
                            UPDATE_TIME_KEY.equalsIgnoreCase(genConfig.getFieldName()));
                    // 是否需要导入导出
                    configItem.set("needExportAndInto", !isKey && this.getNeedExportAndInto(genConfig.getFieldName()));
                    // 是否需要逻辑删除
                    configItem.set("needLogicDelete", DELETE_FLAG_KEY.equalsIgnoreCase(genConfig.getFieldName()));
                    configList.add(configItem);

                });
        // 配置信息
        bindingJsonObject.set("configList", configList);
        // 有排序字段
        bindingJsonObject.set("hasSortCodeField", hasSortCodeField.get());
        return bindingJsonObject;
    }

    private boolean getNeedExportAndInto(String fieldName) {

        return ! StrUtil.equalsAnyIgnoreCase(fieldName,
                SORT_CODE_KEY, CREATE_USER_KEY, CREATE_TIME_KEY, UPDATE_USER_KEY, UPDATE_TIME_KEY, DELETE_FLAG_KEY);
    }
}
