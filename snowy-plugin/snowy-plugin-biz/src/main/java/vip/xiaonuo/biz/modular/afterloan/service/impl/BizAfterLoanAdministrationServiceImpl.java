package vip.xiaonuo.biz.modular.afterloan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.modular.afterloan.entity.BizAfterLoanAnomaly;
import vip.xiaonuo.biz.modular.afterloan.entity.BizAfterLoanNotice;
import vip.xiaonuo.biz.modular.afterloan.mapper.BizAfterLoanAnomalyMapper;
import vip.xiaonuo.biz.modular.afterloan.mapper.BizAfterLoanNoticeMapper;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanAnomalyPageParam;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanSendNoticeParam;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanStatsParam;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanAnomalyPageResult;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanChartItemResult;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanStatsResult;
import vip.xiaonuo.biz.modular.afterloan.service.BizAfterLoanAdministrationService;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 贷后管理服务实现
 */
@Service
public class BizAfterLoanAdministrationServiceImpl implements BizAfterLoanAdministrationService {

    private static final String STATUS_FINISHED = "已处置";
    private static final String STATUS_PROCESSING = "处理中";

    @Resource
    private BizAfterLoanAnomalyMapper bizAfterLoanAnomalyMapper;

    @Resource
    private BizAfterLoanNoticeMapper bizAfterLoanNoticeMapper;

    @Override
    public BizAfterLoanStatsResult stats(BizAfterLoanStatsParam statsParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizAfterLoanStatsParam scopedParam = resolveStatsParam(statsParam, allFarm, visibleFarmIds);

        BizAfterLoanStatsResult result = new BizAfterLoanStatsResult();
        result.setTotal(defaultZero(bizAfterLoanAnomalyMapper.countTotal(scopedParam, allFarm, visibleFarmIds)));
        result.setAnomaly(defaultZero(bizAfterLoanAnomalyMapper.countByStatusNot(scopedParam, STATUS_FINISHED, allFarm, visibleFarmIds)));
        result.setProcessing(defaultZero(bizAfterLoanAnomalyMapper.countByStatus(scopedParam, STATUS_PROCESSING, allFarm, visibleFarmIds)));
        result.setFinished(defaultZero(bizAfterLoanAnomalyMapper.countByStatus(scopedParam, STATUS_FINISHED, allFarm, visibleFarmIds)));

        List<BizAfterLoanChartItemResult> stock = bizAfterLoanAnomalyMapper.stockChart(scopedParam, allFarm, visibleFarmIds);
        List<BizAfterLoanChartItemResult> mortgage = bizAfterLoanAnomalyMapper.mortgageChart(scopedParam, allFarm, visibleFarmIds);
        List<BizAfterLoanChartItemResult> health = bizAfterLoanAnomalyMapper.healthChart(scopedParam, allFarm, visibleFarmIds);

        result.setStock(CollUtil.isNotEmpty(stock) ? stock : defaultStockChart());
        result.setMortgage(CollUtil.isNotEmpty(mortgage) ? mortgage : defaultMortgageChart());
        result.setHealth(CollUtil.isNotEmpty(health) ? health : defaultHealthChart());
        return result;
    }

    @Override
    public Page<BizAfterLoanAnomalyPageResult> anomalyPage(BizAfterLoanAnomalyPageParam pageParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizAfterLoanAnomalyPageParam scopedParam = resolvePageParam(pageParam, allFarm, visibleFarmIds);

        if (!allFarm && StrUtil.isNotBlank(scopedParam.getFarmId()) && !visibleFarmIds.contains(scopedParam.getFarmId())) {
            return new Page<>();
        }
        return (Page<BizAfterLoanAnomalyPageResult>) bizAfterLoanAnomalyMapper.anomalyPage(
                CommonPageRequest.defaultPage(), scopedParam, allFarm, visibleFarmIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sendRectifyNotice(BizAfterLoanSendNoticeParam sendNoticeParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);

        BizAfterLoanAnomaly anomaly = bizAfterLoanAnomalyMapper.selectById(sendNoticeParam.getAnomalyId());
        if (ObjectUtil.isEmpty(anomaly)) {
            throw new CommonException("异常记录不存在，id：{}", sendNoticeParam.getAnomalyId());
        }
        if (!allFarm && !visibleFarmIds.contains(anomaly.getFarmId())) {
            throw new CommonException("您没有权限对该异常发送整改通知，养殖场id：{}", anomaly.getFarmId());
        }

        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        BizAfterLoanNotice notice = new BizAfterLoanNotice();
        notice.setId(IdUtil.fastSimpleUUID());
        notice.setTenantId(ObjectUtil.isNotEmpty(loginUser) ? loginUser.getTenantId() : null);
        notice.setAnomalyId(anomaly.getId());
        notice.setFarmId(anomaly.getFarmId());
        notice.setRequirement(sendNoticeParam.getRequirement());
        notice.setDeadline(sendNoticeParam.getDeadline());
        notice.setReceiver(sendNoticeParam.getReceiver());
        notice.setSendStatus("SENT");
        bizAfterLoanNoticeMapper.insert(notice);

        if (!STATUS_FINISHED.equals(anomaly.getStatus())) {
            anomaly.setStatus(STATUS_PROCESSING);
            bizAfterLoanAnomalyMapper.updateById(anomaly);
        }
    }

    private Long defaultZero(Long value) {
        return ObjectUtil.defaultIfNull(value, 0L);
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

    private BizAfterLoanStatsParam resolveStatsParam(BizAfterLoanStatsParam inputParam, boolean allFarm, List<String> visibleFarmIds) {
        BizAfterLoanStatsParam param = ObjectUtil.defaultIfNull(inputParam, new BizAfterLoanStatsParam());
        if (allFarm) {
            return param;
        }
        if (CollUtil.isEmpty(visibleFarmIds)) {
            throw new CommonException("您没有可用的养殖场数据权限");
        }
        if (StrUtil.isBlank(param.getFarmId())) {
            param.setFarmId(visibleFarmIds.get(0));
            return param;
        }
        if (!visibleFarmIds.contains(param.getFarmId())) {
            throw new CommonException("您没有权限查看该养殖场的数据，养殖场id：{}", param.getFarmId());
        }
        return param;
    }

    private BizAfterLoanAnomalyPageParam resolvePageParam(BizAfterLoanAnomalyPageParam inputParam, boolean allFarm, List<String> visibleFarmIds) {
        BizAfterLoanAnomalyPageParam param = ObjectUtil.defaultIfNull(inputParam, new BizAfterLoanAnomalyPageParam());
        if (allFarm) {
            return param;
        }
        if (CollUtil.isEmpty(visibleFarmIds)) {
            throw new CommonException("您没有可用的养殖场数据权限");
        }
        if (StrUtil.isBlank(param.getFarmId())) {
            param.setFarmId(visibleFarmIds.get(0));
            return param;
        }
        if (!visibleFarmIds.contains(param.getFarmId())) {
            throw new CommonException("您没有权限查看该养殖场的数据，养殖场id：{}", param.getFarmId());
        }
        return param;
    }

    private List<BizAfterLoanChartItemResult> defaultStockChart() {
        List<BizAfterLoanChartItemResult> list = new ArrayList<>();
        list.add(chartItem("在栏", 0L));
        list.add(chartItem("出栏", 0L));
        list.add(chartItem("死亡", 0L));
        return list;
    }

    private List<BizAfterLoanChartItemResult> defaultMortgageChart() {
        List<BizAfterLoanChartItemResult> list = new ArrayList<>();
        list.add(chartItem("应抵押", 0L));
        list.add(chartItem("已抵押", 0L));
        list.add(chartItem("缺口", 0L));
        return list;
    }

    private List<BizAfterLoanChartItemResult> defaultHealthChart() {
        List<BizAfterLoanChartItemResult> list = new ArrayList<>();
        list.add(chartItem("正常", 0L));
        list.add(chartItem("预警", 0L));
        list.add(chartItem("严重预警", 0L));
        return list;
    }

    private BizAfterLoanChartItemResult chartItem(String name, Long value) {
        BizAfterLoanChartItemResult item = new BizAfterLoanChartItemResult();
        item.setName(name);
        item.setValue(value);
        return item;
    }
}
