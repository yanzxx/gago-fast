package vip.xiaonuo.biz.modular.loan.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 贷款申请分页参数
 */
@Getter
@Setter
public class BizLoanApplyPageParam {

    @ApiModelProperty(value = "当前页码")
    private Integer current;

    @ApiModelProperty(value = "每页条数")
    private Integer size;

    @ApiModelProperty(value = "养殖场id")
    private String farmId;

    @ApiModelProperty(value = "申请单号")
    private String applyNo;

    @ApiModelProperty(value = "状态")
    private String loanStatus;
}
