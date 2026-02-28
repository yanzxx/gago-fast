package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 风险阈值结果
 */
@Getter
@Setter
public class BizSupervisionRiskThresholdResult {

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("即将过期预警天数")
    private Integer expiringDays;

    @ApiModelProperty("7天死亡预警阈值")
    private Integer deathCountThreshold;

    @ApiModelProperty("中风险阈值")
    private Integer mediumRiskScore;

    @ApiModelProperty("高风险阈值")
    private Integer highRiskScore;
}
