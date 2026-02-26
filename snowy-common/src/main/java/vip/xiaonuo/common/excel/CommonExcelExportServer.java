package vip.xiaonuo.common.excel;

import vip.xiaonuo.common.page.CommonPageEntity;

import java.util.List;

public interface CommonExcelExportServer {

    List<?> selectListForExcelExport(CommonPageEntity listParam);

}
