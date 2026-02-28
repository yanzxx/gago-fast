package vip.xiaonuo.biz.modular.supervision.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.modular.supervision.mapper.BizSupervisionMapper;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionHomeParam;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionAlertResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionAnomalyResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionHomeResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionMapPointResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionOverviewResult;
import vip.xiaonuo.biz.modular.supervision.service.BizSupervisionService;
import vip.xiaonuo.common.exception.CommonException;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 综合监管服务实现
 */
@Service
public class BizSupervisionServiceImpl implements BizSupervisionService {

    @Resource
    private BizSupervisionMapper bizSupervisionMapper;

    @Override
    public BizSupervisionHomeResult home(BizSupervisionHomeParam homeParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        BizSupervisionHomeParam scopedParam = resolveParam(homeParam, allFarm, visibleFarmIds);

        BizSupervisionHomeResult result = new BizSupervisionHomeResult();
        result.setRefreshTime(DateUtil.now());

        BizSupervisionOverviewResult overview = bizSupervisionMapper.overview(scopedParam, allFarm, visibleFarmIds);
        result.setOverview(ObjectUtil.defaultIfNull(overview, new BizSupervisionOverviewResult()));

        List<BizSupervisionMapPointResult> mapPoints = bizSupervisionMapper.mapPoints(scopedParam, allFarm, visibleFarmIds);
        mapPoints.forEach(this::fillPointCoordinateAndName);
        result.setMapPoints(mapPoints);

        List<BizSupervisionAlertResult> alerts = bizSupervisionMapper.alerts(scopedParam, allFarm, visibleFarmIds);
        result.setAlerts(alerts);

        List<BizSupervisionAnomalyResult> anomalies = bizSupervisionMapper.anomalies(scopedParam, allFarm, visibleFarmIds);
        result.setAnomalies(anomalies);
        return result;
    }

    private void fillPointCoordinateAndName(BizSupervisionMapPointResult point) {
        if (StrUtil.isBlank(point.getFarmName())) {
            point.setFarmName(point.getFarmId());
        }
        String seed = StrUtil.blankToDefault(point.getFarmId(), point.getFarmName());
        int hash = Math.abs(seed.hashCode());
        double longitude = 80 + (hash % 5000) / 100.0;
        double latitude = 20 + ((hash / 100) % 3000) / 100.0;
        point.setLongitude(round2(longitude));
        point.setLatitude(round2(latitude));
        point.setHealthScore(ObjectUtil.defaultIfNull(point.getHealthScore(), 0));
        point.setInStockCount(ObjectUtil.defaultIfNull(point.getInStockCount(), 0L));
        point.setRiskLevel(StrUtil.blankToDefault(point.getRiskLevel(), "normal"));
    }

    private double round2(double value) {
        return Math.round(value * 100D) / 100D;
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

    private BizSupervisionHomeParam resolveParam(BizSupervisionHomeParam inputParam, boolean allFarm, List<String> visibleFarmIds) {
        BizSupervisionHomeParam param = ObjectUtil.defaultIfNull(inputParam, new BizSupervisionHomeParam());
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
}
