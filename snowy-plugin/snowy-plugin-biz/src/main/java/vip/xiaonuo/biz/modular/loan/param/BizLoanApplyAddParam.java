package vip.xiaonuo.biz.modular.loan.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 贷款申请新增参数
 */
@Getter
@Setter
public class BizLoanApplyAddParam {

    @ApiModelProperty(value = "养殖场id", position = 1)
    private String farmId;

    @ApiModelProperty(value = "申请单号", required = true, position = 2)
    @NotBlank(message = "applyNo不能为空")
    private String applyNo;

    @ApiModelProperty(value = "产品编码", required = true, position = 3)
    @NotBlank(message = "productCode不能为空")
    private String productCode;

    @ApiModelProperty(value = "申请金额", required = true, position = 4)
    @NotNull(message = "applyAmount不能为空")
    @DecimalMin(value = "0", message = "applyAmount不能小于0")
    private java.math.BigDecimal applyAmount;

    @ApiModelProperty(value = "申请人", required = true, position = 5)
    @NotBlank(message = "applicantName不能为空")
    private String applicantName;

    @ApiModelProperty(value = "状态", position = 6)
    private String loanStatus;

    @ApiModelProperty(value = "备注", position = 7)
    private String remark;
}
