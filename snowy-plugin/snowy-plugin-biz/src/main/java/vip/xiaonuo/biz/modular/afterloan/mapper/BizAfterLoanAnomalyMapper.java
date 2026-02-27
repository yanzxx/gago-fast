package vip.xiaonuo.biz.modular.afterloan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.afterloan.entity.BizAfterLoanAnomaly;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanAnomalyPageParam;
import vip.xiaonuo.biz.modular.afterloan.param.BizAfterLoanStatsParam;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanAnomalyPageResult;
import vip.xiaonuo.biz.modular.afterloan.result.BizAfterLoanChartItemResult;

import java.util.List;

/**
 * 贷后异常Mapper
 */
public interface BizAfterLoanAnomalyMapper extends BaseMapper<BizAfterLoanAnomaly> {

    IPage<BizAfterLoanAnomalyPageResult> anomalyPage(Page<BizAfterLoanAnomalyPageResult> page,
                                                     @Param("param") BizAfterLoanAnomalyPageParam param,
                                                     @Param("allFarm") boolean allFarm,
                                                     @Param("farmIds") List<String> farmIds);

    Long countTotal(@Param("param") BizAfterLoanStatsParam param,
                    @Param("allFarm") boolean allFarm,
                    @Param("farmIds") List<String> farmIds);

    Long countByStatus(@Param("param") BizAfterLoanStatsParam param,
                       @Param("status") String status,
                       @Param("allFarm") boolean allFarm,
                       @Param("farmIds") List<String> farmIds);

    Long countByStatusNot(@Param("param") BizAfterLoanStatsParam param,
                          @Param("statusNot") String statusNot,
                          @Param("allFarm") boolean allFarm,
                          @Param("farmIds") List<String> farmIds);

    List<BizAfterLoanChartItemResult> stockChart(@Param("param") BizAfterLoanStatsParam param,
                                                 @Param("allFarm") boolean allFarm,
                                                 @Param("farmIds") List<String> farmIds);

    List<BizAfterLoanChartItemResult> mortgageChart(@Param("param") BizAfterLoanStatsParam param,
                                                    @Param("allFarm") boolean allFarm,
                                                    @Param("farmIds") List<String> farmIds);

    List<BizAfterLoanChartItemResult> healthChart(@Param("param") BizAfterLoanStatsParam param,
                                                  @Param("allFarm") boolean allFarm,
                                                  @Param("farmIds") List<String> farmIds);
}
