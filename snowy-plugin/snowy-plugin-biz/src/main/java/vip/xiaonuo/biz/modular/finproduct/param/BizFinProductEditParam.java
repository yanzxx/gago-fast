package vip.xiaonuo.biz.modular.finproduct.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 金融产品编辑参数
 */
@Getter
@Setter
public class BizFinProductEditParam {

    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "产品编码(兼容定位字段)", position = 2)
    private String productCode;

    @ApiModelProperty(value = "农场id", position = 3)
    private String farmId;

    @ApiModelProperty(value = "产品名称", required = true, position = 4)
    @NotBlank(message = "productName不能为空")
    private String productName;

    @ApiModelProperty(value = "适用畜种编码，逗号分隔", required = true, position = 5)
    @NotBlank(message = "speciesCodes不能为空")
    private String speciesCodes;

    @ApiModelProperty(value = "最低额度", required = true, position = 6)
    @NotNull(message = "amountMin不能为空")
    @DecimalMin(value = "0", message = "amountMin不能小于0")
    private java.math.BigDecimal amountMin;

    @ApiModelProperty(value = "最高额度", required = true, position = 7)
    @NotNull(message = "amountMax不能为空")
    @DecimalMin(value = "0", message = "amountMax不能小于0")
    private java.math.BigDecimal amountMax;

    @ApiModelProperty(value = "年化利率", required = true, position = 8)
    @NotNull(message = "annualRate不能为空")
    @DecimalMin(value = "0", message = "annualRate不能小于0")
    private java.math.BigDecimal annualRate;

    @ApiModelProperty(value = "期限(月)", required = true, position = 9)
    @NotNull(message = "termMonths不能为空")
    private Integer termMonths;

    @ApiModelProperty(value = "还款方式", required = true, position = 10)
    @NotBlank(message = "repayType不能为空")
    private String repayType;

    @ApiModelProperty(value = "产品说明", position = 11)
    private String remark;
}
