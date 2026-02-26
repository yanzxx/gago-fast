package vip.xiaonuo.biz.modular.tabular.service;

import vip.xiaonuo.biz.modular.tabular.param.BizTabularDisplayAddParam;

import java.util.List;
import java.util.Map;

public interface BizTabularDisplayService {

    void addDisplayColumn(BizTabularDisplayAddParam tabularDisplayAddParam);

    Map<String, List<String>> getDisplayColumnMap(String userId);
}
