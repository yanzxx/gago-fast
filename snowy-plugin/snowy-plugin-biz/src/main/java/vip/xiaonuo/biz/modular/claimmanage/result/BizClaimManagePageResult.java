package vip.xiaonuo.biz.modular.claimmanage.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 理赔分页结果
 */
@Getter
@Setter
public class BizClaimManagePageResult {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("理赔单号")
    private String claimNo;

    @ApiModelProperty("保单号")
    private String policyNo;

    @ApiModelProperty("被保主体")
    private String insuredName;

    @ApiModelProperty("理赔类型")
    private String claimType;

    @ApiModelProperty("损失数量")
    private Integer lossCount;

    @ApiModelProperty("理赔金额")
    private java.math.BigDecimal claimAmount;

    @ApiModelProperty("出险时间")
    private String occurTime;

    @ApiModelProperty("申请时间")
    private String applyTime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("结果说明")
    private String resultRemark;
}
