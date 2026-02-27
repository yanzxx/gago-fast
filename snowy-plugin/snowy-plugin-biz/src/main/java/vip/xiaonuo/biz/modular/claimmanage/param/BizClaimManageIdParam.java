package vip.xiaonuo.biz.modular.claimmanage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 理赔主键参数
 */
@Getter
@Setter
public class BizClaimManageIdParam {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "理赔单号")
    private String claimNo;
}
