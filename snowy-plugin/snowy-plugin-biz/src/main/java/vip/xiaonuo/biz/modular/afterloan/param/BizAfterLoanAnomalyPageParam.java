package vip.xiaonuo.biz.modular.afterloan.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 贷后异常分页参数
 */
@Getter
@Setter
public class BizAfterLoanAnomalyPageParam extends BizAfterLoanStatsParam {

    @ApiModelProperty(value = "当前页码")
    private Integer current;

    @ApiModelProperty(value = "每页条数")
    private Integer size;
}
