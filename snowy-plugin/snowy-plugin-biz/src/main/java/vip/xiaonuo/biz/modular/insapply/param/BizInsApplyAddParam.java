package vip.xiaonuo.biz.modular.insapply.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 投保记录新增参数
 */
@Getter
@Setter
public class BizInsApplyAddParam {

    @ApiModelProperty(value = "养殖场id", position = 1)
    private String farmId;

    @ApiModelProperty(value = "投保单号", required = true, position = 2)
    @NotBlank(message = "applyNo不能为空")
    private String applyNo;

    @ApiModelProperty(value = "保单号", position = 3)
    private String policyNo;

    @ApiModelProperty(value = "保险产品编码", required = true, position = 4)
    @NotBlank(message = "productCode不能为空")
    private String productCode;

    @ApiModelProperty(value = "被保主体", required = true, position = 5)
    @NotBlank(message = "insuredName不能为空")
    private String insuredName;

    @ApiModelProperty(value = "投保畜种", required = true, position = 6)
    @NotBlank(message = "speciesType不能为空")
    private String speciesType;

    @ApiModelProperty(value = "投保数量", required = true, position = 7)
    @NotNull(message = "insuredCount不能为空")
    @Min(value = 1, message = "insuredCount必须大于0")
    private Integer insuredCount;

    @ApiModelProperty(value = "投保金额", required = true, position = 8)
    @NotNull(message = "insuredAmount不能为空")
    @DecimalMin(value = "0", message = "insuredAmount不能小于0")
    private java.math.BigDecimal insuredAmount;

    @ApiModelProperty(value = "保费金额", required = true, position = 9)
    @NotNull(message = "premiumAmount不能为空")
    @DecimalMin(value = "0", message = "premiumAmount不能小于0")
    private java.math.BigDecimal premiumAmount;

    @ApiModelProperty(value = "状态", position = 10)
    private String status;

    @ApiModelProperty(value = "备注", position = 11)
    private String remark;
}
