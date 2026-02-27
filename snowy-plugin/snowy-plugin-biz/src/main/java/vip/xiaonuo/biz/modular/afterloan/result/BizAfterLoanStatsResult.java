package vip.xiaonuo.biz.modular.afterloan.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 贷后统计结果
 */
@Getter
@Setter
public class BizAfterLoanStatsResult {

    @ApiModelProperty("监控总笔数")
    private Long total = 0L;

    @ApiModelProperty("异常笔数")
    private Long anomaly = 0L;

    @ApiModelProperty("处理中")
    private Long processing = 0L;

    @ApiModelProperty("已处置")
    private Long finished = 0L;

    @ApiModelProperty("存栏统计")
    private List<BizAfterLoanChartItemResult> stock = new ArrayList<>();

    @ApiModelProperty("抵押覆盖")
    private List<BizAfterLoanChartItemResult> mortgage = new ArrayList<>();

    @ApiModelProperty("健康预警")
    private List<BizAfterLoanChartItemResult> health = new ArrayList<>();
}
