package vip.xiaonuo.biz.modular.supervision.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionHomeParam;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionAlertResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionAnomalyResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionFarmStatsResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionMobileTodoRowResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionMapPointResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionOverviewResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeWeeklyStatResult;
import vip.xiaonuo.biz.modular.supervision.result.BizMobileHomeMetricDetailResult;

import java.util.List;

/**
 * 综合监管首页Mapper
 */
public interface BizSupervisionMapper {

    @InterceptorIgnore(tenantLine = "1")
    BizSupervisionOverviewResult overview(@Param("param") BizSupervisionHomeParam param,
                                          @Param("allFarm") boolean allFarm,
                                          @Param("farmIds") List<String> farmIds);

    @InterceptorIgnore(tenantLine = "1")
    List<BizSupervisionMapPointResult> mapPoints(@Param("param") BizSupervisionHomeParam param,
                                                 @Param("allFarm") boolean allFarm,
                                                 @Param("farmIds") List<String> farmIds);

    @InterceptorIgnore(tenantLine = "1")
    List<BizSupervisionAlertResult> alerts(@Param("param") BizSupervisionHomeParam param,
                                           @Param("allFarm") boolean allFarm,
                                           @Param("farmIds") List<String> farmIds);

    @InterceptorIgnore(tenantLine = "1")
    List<BizSupervisionAnomalyResult> anomalies(@Param("param") BizSupervisionHomeParam param,
                                                @Param("allFarm") boolean allFarm,
                                                @Param("farmIds") List<String> farmIds);

    @InterceptorIgnore(tenantLine = "1")
    BizSupervisionFarmStatsResult farmStats(@Param("farmId") String farmId);

    @InterceptorIgnore(tenantLine = "1")
    Long pendingAnomalyCountByKeywords(@Param("param") BizSupervisionHomeParam param,
                                       @Param("allFarm") boolean allFarm,
                                       @Param("farmIds") List<String> farmIds,
                                       @Param("keywords") List<String> keywords);

    @InterceptorIgnore(tenantLine = "1")
    List<BizSupervisionMobileTodoRowResult> pendingTodoRows(@Param("param") BizSupervisionHomeParam param,
                                                            @Param("allFarm") boolean allFarm,
                                                            @Param("farmIds") List<String> farmIds);

    @InterceptorIgnore(tenantLine = "1")
    List<BizMobileHomeWeeklyStatResult> weeklyStats(@Param("param") BizSupervisionHomeParam param,
                                                    @Param("allFarm") boolean allFarm,
                                                    @Param("farmIds") List<String> farmIds);

    @InterceptorIgnore(tenantLine = "1")
    List<BizMobileHomeMetricDetailResult> livestockMetricDetails(@Param("param") BizSupervisionHomeParam param,
                                                                 @Param("allFarm") boolean allFarm,
                                                                 @Param("farmIds") List<String> farmIds,
                                                                 @Param("startDate") String startDate,
                                                                 @Param("endDate") String endDate,
                                                                 @Param("inStockOnly") boolean inStockOnly);

    @InterceptorIgnore(tenantLine = "1")
    List<BizMobileHomeMetricDetailResult> anomalyMetricDetails(@Param("param") BizSupervisionHomeParam param,
                                                               @Param("allFarm") boolean allFarm,
                                                               @Param("farmIds") List<String> farmIds,
                                                               @Param("startDate") String startDate,
                                                               @Param("endDate") String endDate,
                                                               @Param("keyword") String keyword);
}
