package vip.xiaonuo.biz.modular.supervision.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.org.mapper.BizOrgMapper;
import vip.xiaonuo.biz.modular.supervision.mapper.BizSupervisionMapper;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionFarmStatsParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionHomeParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionRiskThresholdParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionRiskThresholdSaveParam;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionAlertResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionAnomalyResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionFarmStatsResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionHomeResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionMapPointResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionOverviewResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionRiskThresholdResult;
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

    private static final String THRESHOLD_KEY = "supervisionRiskThreshold";

    @Resource
    private BizSupervisionMapper bizSupervisionMapper;

    @Resource
    private BizOrgMapper bizOrgMapper;

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

    @Override
    public BizSupervisionFarmStatsResult farmStats(BizSupervisionFarmStatsParam inputParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        String farmId = resolveFarmId(inputParam != null ? inputParam.getFarmId() : null, allFarm, visibleFarmIds);
        BizSupervisionFarmStatsResult result = bizSupervisionMapper.farmStats(farmId);
        if (ObjectUtil.isEmpty(result)) {
            result = new BizSupervisionFarmStatsResult();
            result.setFarmId(farmId);
            result.setFarmName(queryFarm(farmId).getName());
        }
        return result;
    }

    @Override
    public BizSupervisionRiskThresholdResult riskThreshold(BizSupervisionRiskThresholdParam inputParam) {
        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        String farmId = resolveFarmId(inputParam != null ? inputParam.getFarmId() : null, allFarm, visibleFarmIds);

        BizOrg farm = queryFarm(farmId);
        return readThresholdFromExtJson(farmId, farm.getExtJson());
    }

    @Override
    public void saveRiskThreshold(BizSupervisionRiskThresholdSaveParam inputParam) {
        if (inputParam.getHighRiskScore() <= inputParam.getMediumRiskScore()) {
            throw new CommonException("高风险阈值必须大于中风险阈值");
        }

        boolean allFarm = isAllFarmVisible();
        List<String> visibleFarmIds = getVisibleFarmIds(allFarm);
        String farmId = resolveFarmId(inputParam.getFarmId(), allFarm, visibleFarmIds);

        BizOrg farm = queryFarm(farmId);
        JSONObject extJson = parseExtJson(farm.getExtJson());
        JSONObject thresholdJson = new JSONObject();
        thresholdJson.set("expiringDays", inputParam.getExpiringDays());
        thresholdJson.set("deathCountThreshold", inputParam.getDeathCountThreshold());
        thresholdJson.set("mediumRiskScore", inputParam.getMediumRiskScore());
        thresholdJson.set("highRiskScore", inputParam.getHighRiskScore());
        extJson.set(THRESHOLD_KEY, thresholdJson);

        BizOrg update = new BizOrg();
        update.setId(farmId);
        update.setExtJson(JSONUtil.toJsonStr(extJson));
        bizOrgMapper.updateById(update);
    }

    private void fillPointCoordinateAndName(BizSupervisionMapPointResult point) {
        if (StrUtil.isBlank(point.getFarmName())) {
            point.setFarmName(point.getFarmId());
        }
        Double longitude = tryGetCoordinate(point.getExtJson(), "longitude", "lng", "lon");
        Double latitude = tryGetCoordinate(point.getExtJson(), "latitude", "lat");
        if (ObjectUtil.isNotEmpty(longitude) && ObjectUtil.isNotEmpty(latitude)) {
            point.setLongitude(round2(longitude));
            point.setLatitude(round2(latitude));
        } else {
            String seed = StrUtil.blankToDefault(point.getFarmId(), point.getFarmName());
            int hash = Math.abs(seed.hashCode());
            double fallbackLongitude = 80 + (hash % 5000) / 100.0;
            double fallbackLatitude = 20 + ((hash / 100) % 3000) / 100.0;
            point.setLongitude(round2(fallbackLongitude));
            point.setLatitude(round2(fallbackLatitude));
        }
        point.setHealthScore(ObjectUtil.defaultIfNull(point.getHealthScore(), 0));
        point.setInStockCount(ObjectUtil.defaultIfNull(point.getInStockCount(), 0L));
        point.setRiskLevel(StrUtil.blankToDefault(point.getRiskLevel(), "normal"));
    }

    private Double tryGetCoordinate(String extJsonStr, String... keys) {
        JSONObject extJson = parseExtJson(extJsonStr);
        for (String key : keys) {
            Object val = extJson.get(key);
            if (ObjectUtil.isEmpty(val)) {
                continue;
            }
            try {
                return Double.parseDouble(val.toString());
            } catch (Exception ignored) {
                // continue next key
            }
        }
        JSONObject location = extJson.getJSONObject("location");
        if (ObjectUtil.isNotEmpty(location)) {
            for (String key : keys) {
                Object val = location.get(key);
                if (ObjectUtil.isEmpty(val)) {
                    continue;
                }
                try {
                    return Double.parseDouble(val.toString());
                } catch (Exception ignored) {
                    // continue next key
                }
            }
        }
        return null;
    }

    private double round2(double value) {
        return Math.round(value * 100D) / 100D;
    }

    private boolean isAllFarmVisible() {
        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        return ObjectUtil.isNotEmpty(loginUser) && CollUtil.contains(loginUser.getRoleCodeList(), "superAdmin");
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

    private BizSupervisionRiskThresholdResult readThresholdFromExtJson(String farmId, String extJsonStr) {
        BizSupervisionRiskThresholdResult result = defaultThreshold(farmId);
        JSONObject extJson = parseExtJson(extJsonStr);
        JSONObject thresholdJson = extJson.getJSONObject(THRESHOLD_KEY);
        if (ObjectUtil.isEmpty(thresholdJson)) {
            return result;
        }
        result.setExpiringDays(thresholdJson.getInt("expiringDays", result.getExpiringDays()));
        result.setDeathCountThreshold(thresholdJson.getInt("deathCountThreshold", result.getDeathCountThreshold()));
        result.setMediumRiskScore(thresholdJson.getInt("mediumRiskScore", result.getMediumRiskScore()));
        result.setHighRiskScore(thresholdJson.getInt("highRiskScore", result.getHighRiskScore()));
        return result;
    }

    private JSONObject parseExtJson(String extJsonStr) {
        if (StrUtil.isBlank(extJsonStr) || !JSONUtil.isTypeJSON(extJsonStr)) {
            return new JSONObject();
        }
        try {
            return JSONUtil.parseObj(extJsonStr);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private BizSupervisionRiskThresholdResult defaultThreshold(String farmId) {
        BizSupervisionRiskThresholdResult result = new BizSupervisionRiskThresholdResult();
        result.setFarmId(farmId);
        result.setExpiringDays(7);
        result.setDeathCountThreshold(10);
        result.setMediumRiskScore(60);
        result.setHighRiskScore(80);
        return result;
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
