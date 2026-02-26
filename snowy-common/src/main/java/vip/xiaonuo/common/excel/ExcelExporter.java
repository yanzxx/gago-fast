package vip.xiaonuo.common.excel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.fhs.core.trans.vo.TransPojo;
import com.fhs.core.trans.vo.VO;
import com.fhs.trans.service.impl.TransService;
import org.apache.poi.ss.usermodel.DataFormat;
import org.springframework.util.Assert;
import vip.xiaonuo.common.annotation.ExcelConfig;
import vip.xiaonuo.common.page.CommonPageEntity;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelExporter {

    private final Class<? extends VO> pojoClass;

    private CommonExcelExportServer excelExportServer;

    private CommonPageEntity listParam;

    private Integer rowAccessWindowSize = 1000;

    private OutputStream outputStream;

    private Boolean isClose = true;

    private ExcelExporter(Class<? extends VO> pojoClass) {
        this.pojoClass = pojoClass;
    }

    public static ExcelExporter pojoClass(Class<? extends TransPojo> pojoClass) {
        Assert.notNull(pojoClass, "pojoClass 不能为null");

        return new ExcelExporter(pojoClass);
    }

    public ExcelExporter rowService(CommonExcelExportServer excelExportServer) {
        Assert.notNull(excelExportServer, "excelExportServer 不能为null");

        this.excelExportServer = excelExportServer;
        return this;
    }

    public ExcelExporter listParam(CommonPageEntity listParam) {
        Assert.notNull(listParam, "listParam 不能为null");

        this.listParam = listParam;
        return this;
    }

    public ExcelExporter rowAccessWindowSize(int rowAccessWindowSize) {
        this.rowAccessWindowSize = rowAccessWindowSize;
        return this;
    }


    public ExcelExporter outputStream(OutputStream outputStream) {

        return this.outputStream(outputStream, true);
    }

    public ExcelExporter outputStream(OutputStream outputStream, Boolean isClose) {
        Assert.notNull(outputStream, "outputStream 不能为null");
        Assert.notNull(isClose, "isClose 不能为null");

        this.outputStream = outputStream;
        this.isClose = isClose;
        return this;
    }

    public void export() {
        this.export(ExcelUtil.getBigWriter(rowAccessWindowSize));
    }

    public void export(BigExcelWriter bigWriter) {
        // 设置表头
        bigWriter.setHeaderAlias(ExcelExporter.getExcelHeaderMap(pojoClass));
        // 只保留别名中的字段值
        bigWriter.setOnlyAlias(true);
        // 设置 全局日期格式
        // 其他格式如：字符串格式、数字格式参考日期格式设置
        this.setGlobalDateStyle(bigWriter);

        // 获取数据
        List<?> rowList = excelExportServer.selectListForExcelExport(listParam);
        while (CollectionUtil.isNotEmpty(rowList)) {
            List<? extends VO> list = BeanUtil.copyToList(rowList, pojoClass);

            // 翻译数据
            SpringUtil.getBean(TransService.class).transBatch(list);

            // 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件
            bigWriter.write(list);

            listParam.setCurrent(listParam.getCurrent() + 1);
            rowList = excelExportServer.selectListForExcelExport(listParam);
        }

        // 设置所有列为自动宽度，不考虑合并单元格
        bigWriter.autoSizeColumnAll();
        // 将Excel Workbook刷出到输出流
        bigWriter.flush(outputStream, isClose);
        // 关闭writer，释放内存
        bigWriter.close();

    }

    private void setGlobalDateStyle(BigExcelWriter bigWriter) {
        DataFormat dataFormat = bigWriter.getWorkbook().createDataFormat();
        short format = dataFormat.getFormat(DatePattern.NORM_DATETIME_PATTERN);
        bigWriter.getStyleSet().getCellStyleForDate().setDataFormat(format);
    }

    /**
     * 返回表头Map
     *
     * @param pojoClass
     * @return
     */
    private static Map<String, String> getExcelHeaderMap(Class<?> pojoClass) {
        Map<String, String> map = new LinkedHashMap<>();

        Field[] fields = ReflectUtil.getFields(pojoClass, field -> field.getAnnotation(ExcelConfig.class) != null);

        Arrays.stream(fields).forEach(field -> map.put(field.getName(), field.getAnnotation(ExcelConfig.class).name()));

        return map;

    }

}
