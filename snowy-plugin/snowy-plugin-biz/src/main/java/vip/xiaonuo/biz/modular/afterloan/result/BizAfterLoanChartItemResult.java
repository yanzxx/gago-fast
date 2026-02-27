package vip.xiaonuo.biz.modular.afterloan.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 贷后图表项
 */
@Getter
@Setter
public class BizAfterLoanChartItemResult {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("数值")
    private Long value;
}
