package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 风险预警项
 */
@Getter
@Setter
public class BizSupervisionAlertResult {

    @ApiModelProperty("异常id")
    private String id;

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("风险等级")
    private String riskLevel;

    @ApiModelProperty("触发时间")
    private String triggerTime;

    @ApiModelProperty("预警内容")
    private String content;
}
