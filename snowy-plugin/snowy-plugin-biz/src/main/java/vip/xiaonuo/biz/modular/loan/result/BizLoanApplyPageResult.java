package vip.xiaonuo.biz.modular.loan.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 贷款申请列表项
 */
@Getter
@Setter
public class BizLoanApplyPageResult {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("申请单号")
    private String applyNo;

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("产品编码")
    private String productCode;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("申请金额")
    private java.math.BigDecimal applyAmount;

    @ApiModelProperty("申请人")
    private String applicantName;

    @ApiModelProperty("状态")
    private String loanStatus;

    @ApiModelProperty("申请时间")
    private String applyTime;
}
