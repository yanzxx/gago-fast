package vip.xiaonuo.biz.modular.supervision.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.org.mapper.BizOrgMapper;
import vip.xiaonuo.biz.modular.supervision.mapper.BizSupervisionMapper;
import vip.xiaonuo.biz.modular.supervision.param.BizMobileHomeParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionHomeParam;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeHeaderResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeMetricsResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeTodoItemResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeTodoSummaryResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionMobileTodoRowResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionOverviewResult;
import vip.xiaonuo.biz.modular.supervision.service.BizMobileHomeService;
import vip.xiaonuo.common.exception.CommonException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 移动端首页服务实现
 */
@Service
public class BizMobileHomeServiceImpl implements BizMobileHomeService {

    private static final String TODO_TYPE_ESTRUS = "ESTRUS";
    private static final String TODO_TYPE_BREED = "BREED";
    private static final String TODO_TYPE_ILLEGAL_OUT = "ILLEGAL_OUT";

    @Resource
    private BizSupervisionMapper bizSupervisionMapper;

    @Resource
    private BizOrgMapper bizOrgMapper;

    @Override
    public BizMobileHomeHeaderResult header(BizMobileHomeParam param) {
        ScopeContext scopeContext = resolveScopeContext(param);
        BizOrg farm = queryFarm(scopeContext.getFarmId());
        BizMobileHomeHeaderResult result = new BizMobileHomeHeaderResult();
        result.setOrgId(farm.getId());
        result.setOrgName(StrUtil.blankToDefault(farm.getName(), farm.getId()));
        result.setRefreshTime(DateUtil.now());
        return result;
    }

    @Override
    public BizMobileHomeMetricsResult metrics(BizMobileHomeParam param) {
        ScopeContext scopeContext = resolveScopeContext(param);
        BizSupervisionOverviewResult overview = bizSupervisionMapper.overview(
                scopeContext.getHomeParam(), scopeContext.isAllFarm(), scopeContext.getVisibleFarmIds()
        );
        overview = ObjectUtil.defaultIfNull(overview, new BizSupervisionOverviewResult());

        BizMobileHomeMetricsResult result = new BizMobileHomeMetricsResult();
        Long inStockCount = ObjectUtil.defaultIfNull(overview.getInStockCount(), 0L);
        Long outStockCount = ObjectUtil.defaultIfNull(overview.getOutStockCount(), 0L);
        Long deadCount = ObjectUtil.defaultIfNull(overview.getDeadCount(), 0L);
        result.setInStockCount(inStockCount);
        result.setTotalStockCount(inStockCount + outStockCount + deadCount);

        result.setDeviceAnomalyCount(safeCountByKeywords(scopeContext, Arrays.asList("设备")));
        result.setCollarAnomalyCount(safeCountByKeywords(scopeContext, Arrays.asList("项圈")));
        return result;
    }

    @Override
    public List<BizMobileHomeTodoSummaryResult> todos(BizMobileHomeParam param, Integer topN) {
        ScopeContext scopeContext = resolveScopeContext(param);
        int validTopN = topN == null || topN < 1 ? 3 : Math.min(topN, 10);
        List<BizSupervisionMobileTodoRowResult> rows = bizSupervisionMapper.pendingTodoRows(
                scopeContext.getHomeParam(), scopeContext.isAllFarm(), scopeContext.getVisibleFarmIds()
        );
        if (CollUtil.isEmpty(rows)) {
            return defaultTodoSummaryList();
        }

        Map<String, BizMobileHomeTodoSummaryResult> summaryMap = defaultTodoSummaryMap();
        for (BizSupervisionMobileTodoRowResult row : rows) {
            BizMobileHomeTodoSummaryResult summary = summaryMap.get(row.getTodoType());
            if (summary == null) {
                continue;
            }
            summary.setCount(summary.getCount() + 1);
            if (summary.getItems().size() < validTopN) {
                summary.getItems().add(toTodoItem(row));
            }
        }
        return new ArrayList<>(summaryMap.values());
    }

    @Override
    public List<BizMobileHomeTodoItemResult> todoDetails(BizMobileHomeParam param, String todoType) {
        ScopeContext scopeContext = resolveScopeContext(param);
        List<BizSupervisionMobileTodoRowResult> rows = bizSupervisionMapper.pendingTodoRows(
                scopeContext.getHomeParam(), scopeContext.isAllFarm(), scopeContext.getVisibleFarmIds()
        );
        if (CollUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        if (StrUtil.isBlank(todoType)) {
            return rows.stream().map(this::toTodoItem).collect(Collectors.toList());
        }
        return rows.stream()
                .filter(row -> StrUtil.equalsIgnoreCase(todoType, row.getTodoType()))
                .map(this::toTodoItem)
                .collect(Collectors.toList());
    }

    private long safeCountByKeywords(ScopeContext scopeContext, List<String> keywords) {
        Long count = bizSupervisionMapper.pendingAnomalyCountByKeywords(
                scopeContext.getHomeParam(), scopeContext.isAllFarm(), scopeContext.getVisibleFarmIds(), keywords
        );
        return ObjectUtil.defaultIfNull(count, 0L);
    }

    private BizMobileHomeTodoItemResult toTodoItem(BizSupervisionMobileTodoRowResult row) {
        BizMobileHomeTodoItemResult item = new BizMobileHomeTodoItemResult();
        item.setId(row.getId());
        item.setFarmId(row.getFarmId());
        item.setAnomalyType(row.getAnomalyType());
        item.setTriggerTime(row.getTriggerTime());
        item.setStatus(row.getStatus());
        item.setContent(row.getContent());
        return item;
    }

    private List<BizMobileHomeTodoSummaryResult> defaultTodoSummaryList() {
        return new ArrayList<>(defaultTodoSummaryMap().values());
    }

    private Map<String, BizMobileHomeTodoSummaryResult> defaultTodoSummaryMap() {
        LinkedHashMap<String, BizMobileHomeTodoSummaryResult> result = new LinkedHashMap<>();
        result.put(TODO_TYPE_ESTRUS, buildSummary(TODO_TYPE_ESTRUS, "发情提醒"));
        result.put(TODO_TYPE_BREED, buildSummary(TODO_TYPE_BREED, "配种提醒"));
        result.put(TODO_TYPE_ILLEGAL_OUT, buildSummary(TODO_TYPE_ILLEGAL_OUT, "非法离栏"));
        return result;
    }

    private BizMobileHomeTodoSummaryResult buildSummary(String type, String name) {
        BizMobileHomeTodoSummaryResult result = new BizMobileHomeTodoSummaryResult();
        result.setTodoType(type);
        result.setTodoName(name);
        return result;
    }

    private ScopeContext resolveScopeContext(BizMobileHomeParam inputParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizSupervisionHomeParam homeParam = new BizSupervisionHomeParam();
        if (inputParam != null) {
            homeParam.setFarmId(inputParam.getFarmId());
        }
        resolveParam(homeParam, allFarm, visibleFarmIds);
        return new ScopeContext(allFarm, visibleFarmIds, homeParam);
    }

    private boolean isAllFarmVisible() {
        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        return ObjectUtil.isNotEmpty(loginUser) && CollUtil.contains(loginUser.getRoleCodeList(), "superAdmin");
    }

    private List<String> getVisibleFarmIds(boolean allFarm) {
        if (allFarm) {
            return Collections.emptyList();
        }
        List<String> dataScopeFarmIds = StpLoginUserUtil.getLoginUserDataScope();
        if (CollUtil.isNotEmpty(dataScopeFarmIds)) {
            return dataScopeFarmIds.stream().distinct().collect(Collectors.toList());
        }
        String farmId = StpLoginUserUtil.getLoginUser().getOrgId();
        return StrUtil.isNotBlank(farmId) ? Collections.singletonList(farmId) : Collections.emptyList();
    }

    private void resolveParam(BizSupervisionHomeParam param, boolean allFarm, List<String> visibleFarmIds) {
        if (allFarm) {
            return;
        }
        if (CollUtil.isEmpty(visibleFarmIds)) {
            throw new CommonException("您没有可用的养殖场数据权限");
        }
        if (StrUtil.isBlank(param.getFarmId())) {
            param.setFarmId(visibleFarmIds.get(0));
            return;
        }
        if (!visibleFarmIds.contains(param.getFarmId())) {
            throw new CommonException("您没有权限查看该养殖场的数据，养殖场id：{}", param.getFarmId());
        }
    }

    private String resolveFarmId(String inputFarmId, boolean allFarm, List<String> visibleFarmIds) {
        if (allFarm) {
            if (StrUtil.isNotBlank(inputFarmId)) {
                return inputFarmId;
            }
            String loginOrgId = StpLoginUserUtil.getLoginUser().getOrgId();
            if (StrUtil.isNotBlank(loginOrgId)) {
                return loginOrgId;
            }
            throw new CommonException("请先选择养殖场");
        }
        if (CollUtil.isEmpty(visibleFarmIds)) {
            throw new CommonException("您没有可用的养殖场数据权限");
        }
        if (StrUtil.isBlank(inputFarmId)) {
            return visibleFarmIds.get(0);
        }
        if (!visibleFarmIds.contains(inputFarmId)) {
            throw new CommonException("您没有权限查看该养殖场的数据，养殖场id：{}", inputFarmId);
        }
        return inputFarmId;
    }

    private BizOrg queryFarm(String farmId) {
        BizOrg farm = bizOrgMapper.selectById(farmId);
        if (ObjectUtil.isEmpty(farm)) {
            throw new CommonException("养殖场不存在，id：{}", farmId);
        }
        return farm;
    }

    private static class ScopeContext {
        private final boolean allFarm;
        private final List<String> visibleFarmIds;
        private final BizSupervisionHomeParam homeParam;

        private ScopeContext(boolean allFarm, List<String> visibleFarmIds, BizSupervisionHomeParam homeParam) {
            this.allFarm = allFarm;
            this.visibleFarmIds = visibleFarmIds;
            this.homeParam = homeParam;
        }

        private boolean isAllFarm() {
            return allFarm;
        }

        private List<String> getVisibleFarmIds() {
            return visibleFarmIds;
        }

        private BizSupervisionHomeParam getHomeParam() {
            return homeParam;
        }

        private String getFarmId() {
            return homeParam.getFarmId();
        }
    }
}
