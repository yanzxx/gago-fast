package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端核心指标详情
 */
@Getter
@Setter
public class BizMobileHomeMetricDetailResult {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("指标类型")
    private String metricType;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("时间")
    private String eventTime;
}
