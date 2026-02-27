package vip.xiaonuo.biz.modular.claimmanage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 理赔分页参数
 */
@Getter
@Setter
public class BizClaimManagePageParam {

    @ApiModelProperty(value = "当前页码")
    private Integer current;

    @ApiModelProperty(value = "每页条数")
    private Integer size;

    @ApiModelProperty(value = "养殖场id")
    private String farmId;

    @ApiModelProperty(value = "理赔单号")
    private String claimNo;

    @ApiModelProperty(value = "保单号")
    private String policyNo;

    @ApiModelProperty(value = "状态")
    private String status;
}
