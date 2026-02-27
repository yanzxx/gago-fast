package vip.xiaonuo.biz.modular.insproduct.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 保险产品编辑参数
 */
@Getter
@Setter
public class BizInsProductEditParam {

    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    @ApiModelProperty(value = "养殖场id", position = 2)
    private String farmId;

    @ApiModelProperty(value = "产品名称", required = true, position = 3)
    @NotBlank(message = "productName不能为空")
    private String productName;

    @ApiModelProperty(value = "险种类型", required = true, position = 4)
    @NotBlank(message = "insuranceType不能为空")
    private String insuranceType;

    @ApiModelProperty(value = "保障范围", required = true, position = 5)
    @NotBlank(message = "coverageScope不能为空")
    private String coverageScope;

    @ApiModelProperty(value = "费率", required = true, position = 6)
    @NotNull(message = "premiumRate不能为空")
    @DecimalMin(value = "0", message = "premiumRate不能小于0")
    private java.math.BigDecimal premiumRate;

    @ApiModelProperty(value = "最高赔付金额", required = true, position = 7)
    @NotNull(message = "maxCompensation不能为空")
    @DecimalMin(value = "0", message = "maxCompensation不能小于0")
    private java.math.BigDecimal maxCompensation;

    @ApiModelProperty(value = "备注", position = 8)
    private String remark;
}
