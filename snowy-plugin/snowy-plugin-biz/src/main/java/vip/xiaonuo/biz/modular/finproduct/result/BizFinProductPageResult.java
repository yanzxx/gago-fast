package vip.xiaonuo.biz.modular.finproduct.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 金融产品列表项
 */
@Getter
@Setter
public class BizFinProductPageResult {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("产品主键ID(兼容字段)")
    private String productId;

    @ApiModelProperty("农场id")
    private String farmId;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("产品编码")
    private String productCode;

    @ApiModelProperty("适用畜种")
    private String speciesCodes;

    @ApiModelProperty("最低额度")
    private java.math.BigDecimal amountMin;

    @ApiModelProperty("最高额度")
    private java.math.BigDecimal amountMax;

    @ApiModelProperty("年化利率")
    private java.math.BigDecimal annualRate;

    @ApiModelProperty("期限(月)")
    private Integer termMonths;

    @ApiModelProperty("还款方式")
    private String repayType;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("更新时间")
    private String updateTime;
}
