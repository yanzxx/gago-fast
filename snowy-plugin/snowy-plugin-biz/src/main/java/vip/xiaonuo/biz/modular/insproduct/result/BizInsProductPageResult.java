package vip.xiaonuo.biz.modular.insproduct.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 保险产品列表项
 */
@Getter
@Setter
public class BizInsProductPageResult {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("农场id")
    private String farmId;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("产品编码")
    private String productCode;

    @ApiModelProperty("险种类型")
    private String insuranceType;

    @ApiModelProperty("保障范围")
    private String coverageScope;

    @ApiModelProperty("费率")
    private java.math.BigDecimal premiumRate;

    @ApiModelProperty("最高赔付金额")
    private java.math.BigDecimal maxCompensation;

    @ApiModelProperty("状态")
    private String status;
}
