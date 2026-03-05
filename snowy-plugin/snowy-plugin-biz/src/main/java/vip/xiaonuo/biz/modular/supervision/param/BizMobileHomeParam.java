package vip.xiaonuo.biz.modular.supervision.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端首页参数
 */
@Getter
@Setter
public class BizMobileHomeParam {

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("指标类型：totalStockCount/inStockCount/deviceAnomalyCount/collarAnomalyCount")
    private String metricType;

    @ApiModelProperty("开始日期，格式yyyy-MM-dd")
    private String startDate;

    @ApiModelProperty("结束日期，格式yyyy-MM-dd")
    private String endDate;
}
