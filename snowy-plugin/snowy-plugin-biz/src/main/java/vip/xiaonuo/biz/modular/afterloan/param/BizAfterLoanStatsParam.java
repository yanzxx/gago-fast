package vip.xiaonuo.biz.modular.afterloan.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 贷后统计参数
 */
@Getter
@Setter
public class BizAfterLoanStatsParam {

    @ApiModelProperty(value = "养殖场id")
    private String farmId;

    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "风险等级")
    private String riskLevel;
}
