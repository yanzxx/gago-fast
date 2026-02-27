package vip.xiaonuo.biz.modular.claimmanage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 理赔新增参数
 */
@Getter
@Setter
public class BizClaimManageAddParam {

    @ApiModelProperty(value = "养殖场id")
    private String farmId;

    @ApiModelProperty(value = "理赔单号", required = true)
    @NotBlank(message = "claimNo不能为空")
    private String claimNo;

    @ApiModelProperty(value = "保单号", required = true)
    @NotBlank(message = "policyNo不能为空")
    private String policyNo;

    @ApiModelProperty(value = "投保单号")
    private String applyNo;

    @ApiModelProperty(value = "被保主体", required = true)
    @NotBlank(message = "insuredName不能为空")
    private String insuredName;

    @ApiModelProperty(value = "理赔类型", required = true)
    @NotBlank(message = "claimType不能为空")
    private String claimType;

    @ApiModelProperty(value = "损失数量", required = true)
    @NotNull(message = "lossCount不能为空")
    private Integer lossCount;

    @ApiModelProperty(value = "理赔金额", required = true)
    @NotNull(message = "claimAmount不能为空")
    private java.math.BigDecimal claimAmount;

    @ApiModelProperty(value = "出险时间", required = true)
    @NotBlank(message = "occurTime不能为空")
    private String occurTime;

    @ApiModelProperty(value = "附件材料")
    private String evidenceFiles;

    @ApiModelProperty(value = "备注")
    private String remark;
}
