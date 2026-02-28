package vip.xiaonuo.biz.modular.supervision.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 风险阈值查询参数
 */
@Getter
@Setter
public class BizSupervisionRiskThresholdParam {

    @ApiModelProperty("养殖场id")
    private String farmId;
}
