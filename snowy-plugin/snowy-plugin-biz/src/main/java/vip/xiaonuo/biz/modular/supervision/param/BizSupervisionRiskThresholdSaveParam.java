package vip.xiaonuo.biz.modular.supervision.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 风险阈值保存参数
 */
@Getter
@Setter
public class BizSupervisionRiskThresholdSaveParam {

    @ApiModelProperty("养殖场id")
    private String farmId;

    @NotNull(message = "即将过期预警天数不能为空")
    @Min(value = 1, message = "即将过期预警天数最小为1")
    @Max(value = 60, message = "即将过期预警天数最大为60")
    @ApiModelProperty("即将过期预警天数")
    private Integer expiringDays;

    @NotNull(message = "7天死亡预警阈值不能为空")
    @Min(value = 1, message = "7天死亡预警阈值最小为1")
    @Max(value = 9999, message = "7天死亡预警阈值最大为9999")
    @ApiModelProperty("7天死亡预警阈值")
    private Integer deathCountThreshold;

    @NotNull(message = "中风险阈值不能为空")
    @Min(value = 1, message = "中风险阈值最小为1")
    @Max(value = 100, message = "中风险阈值最大为100")
    @ApiModelProperty("中风险阈值")
    private Integer mediumRiskScore;

    @NotNull(message = "高风险阈值不能为空")
    @Min(value = 1, message = "高风险阈值最小为1")
    @Max(value = 100, message = "高风险阈值最大为100")
    @ApiModelProperty("高风险阈值")
    private Integer highRiskScore;
}
