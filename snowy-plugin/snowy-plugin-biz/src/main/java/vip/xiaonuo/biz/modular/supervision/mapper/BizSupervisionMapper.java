package vip.xiaonuo.biz.modular.supervision.mapper;

import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.supervision.param.BizSupervisionHomeParam;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionAlertResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionAnomalyResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionMapPointResult;
import vip.xiaonuo.biz.modular.supervision.result.BizSupervisionOverviewResult;

import java.util.List;

/**
 * 综合监管首页Mapper
 */
public interface BizSupervisionMapper {

    BizSupervisionOverviewResult overview(@Param("param") BizSupervisionHomeParam param,
                                          @Param("allFarm") boolean allFarm,
                                          @Param("farmIds") List<String> farmIds);

    List<BizSupervisionMapPointResult> mapPoints(@Param("param") BizSupervisionHomeParam param,
                                                 @Param("allFarm") boolean allFarm,
                                                 @Param("farmIds") List<String> farmIds);

    List<BizSupervisionAlertResult> alerts(@Param("param") BizSupervisionHomeParam param,
                                           @Param("allFarm") boolean allFarm,
                                           @Param("farmIds") List<String> farmIds);

    List<BizSupervisionAnomalyResult> anomalies(@Param("param") BizSupervisionHomeParam param,
                                                @Param("allFarm") boolean allFarm,
                                                @Param("farmIds") List<String> farmIds);
}
