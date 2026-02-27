package vip.xiaonuo.biz.modular.afterloan.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 贷后异常列表项
 */
@Getter
@Setter
public class BizAfterLoanAnomalyPageResult {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("异常类型")
    private String anomalyType;

    @ApiModelProperty("异常对象")
    private String targetNo;

    @ApiModelProperty("触发时间")
    private String triggerTime;

    @ApiModelProperty("风险等级")
    private String riskLevel;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("异常描述")
    private String description;
}
