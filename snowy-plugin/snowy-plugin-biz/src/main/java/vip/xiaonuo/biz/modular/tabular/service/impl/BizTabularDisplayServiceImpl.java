package vip.xiaonuo.biz.modular.tabular.service.impl;

import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.modular.tabular.param.BizTabularDisplayAddParam;
import vip.xiaonuo.biz.modular.tabular.service.BizTabularDisplayService;
import vip.xiaonuo.common.cache.CommonCacheOperator;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BizTabularDisplayServiceImpl implements BizTabularDisplayService {

    @Resource
    private CommonCacheOperator commonCacheOperator;

    private static final String TABULAR_DISPLAY_COLUMN_CACHE_KEY = "tabular-display-column:";


    @Override
    public void addDisplayColumn(BizTabularDisplayAddParam tabularDisplayAddParam) {
        String userId = StpLoginUserUtil.getUserId();
        String formId = tabularDisplayAddParam.getFormId();
        List<String> displayList = tabularDisplayAddParam.getDisplayList();

        String key = TABULAR_DISPLAY_COLUMN_CACHE_KEY + userId;

        Map<String, Object> cacheMap = new HashMap<>();
        cacheMap.put(formId, displayList);

        commonCacheOperator.putMap(key, cacheMap);
    }

    @Override
    public Map<String, List<String>> getDisplayColumnMap(String userId) {
        String cacheKey = TABULAR_DISPLAY_COLUMN_CACHE_KEY + userId;

        Map<Object, Object> cacheOperatorMap = commonCacheOperator.getMap(cacheKey);
        Map<String, List<String>> displayMap = new HashMap<>(cacheOperatorMap.size());
        cacheOperatorMap.forEach((key, obj) -> displayMap.put(key.toString(), (List<String>) obj));

        return displayMap;
    }
}

