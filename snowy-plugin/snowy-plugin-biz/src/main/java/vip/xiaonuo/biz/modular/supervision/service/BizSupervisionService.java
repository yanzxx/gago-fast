package vip.xiaonuo.biz.modular.supervision.service;

import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionHomeParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionFarmStatsParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionRiskThresholdParam;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionRiskThresholdSaveParam;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionFarmStatsResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionHomeResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionRiskThresholdResult;

/**
 * 综合监管服务
 */
public interface BizSupervisionService {

    /**
     * 首页大屏数据
     */
    BizSupervisionHomeResult home(BizSupervisionHomeParam homeParam);

    /**
     * 风险阈值详情
     */
    BizSupervisionRiskThresholdResult riskThreshold(BizSupervisionRiskThresholdParam param);

    /**
     * 农场经营健康与贷款统计
     */
    BizSupervisionFarmStatsResult farmStats(BizSupervisionFarmStatsParam param);

    /**
     * 保存风险阈值
     */
    void saveRiskThreshold(BizSupervisionRiskThresholdSaveParam param);
}
