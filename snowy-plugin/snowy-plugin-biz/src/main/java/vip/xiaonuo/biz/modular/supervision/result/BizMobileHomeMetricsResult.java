package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端首页核心指标
 */
@Getter
@Setter
public class BizMobileHomeMetricsResult {

    @ApiModelProperty("总存栏量")
    private Long totalStockCount = 0L;

    @ApiModelProperty("在栏数")
    private Long inStockCount = 0L;

    @ApiModelProperty("设备异常数")
    private Long deviceAnomalyCount = 0L;

    @ApiModelProperty("项圈异常数")
    private Long collarAnomalyCount = 0L;
}
