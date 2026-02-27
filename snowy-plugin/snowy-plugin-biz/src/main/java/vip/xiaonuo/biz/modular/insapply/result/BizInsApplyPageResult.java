package vip.xiaonuo.biz.modular.insapply.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 投保记录列表项
 */
@Getter
@Setter
public class BizInsApplyPageResult {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("投保单号")
    private String applyNo;

    @ApiModelProperty("保单号")
    private String policyNo;

    @ApiModelProperty("保险产品编码")
    private String productCode;

    @ApiModelProperty("被保主体")
    private String insuredName;

    @ApiModelProperty("投保畜种")
    private String speciesType;

    @ApiModelProperty("投保数量")
    private Integer insuredCount;

    @ApiModelProperty("投保金额")
    private java.math.BigDecimal insuredAmount;

    @ApiModelProperty("保费金额")
    private java.math.BigDecimal premiumAmount;

    @ApiModelProperty("状态")
    private String status;
}
