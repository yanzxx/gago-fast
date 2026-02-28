package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 异常列表项
 */
@Getter
@Setter
public class BizSupervisionAnomalyResult {

    @ApiModelProperty("异常id")
    private String id;

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("异常类型")
    private String anomalyType;

    @ApiModelProperty("风险等级")
    private String riskLevel;

    @ApiModelProperty("触发时间")
    private String triggerTime;

    @ApiModelProperty("处置状态")
    private String status;

    @ApiModelProperty("整改通知状态")
    private String noticeStatus;
}
