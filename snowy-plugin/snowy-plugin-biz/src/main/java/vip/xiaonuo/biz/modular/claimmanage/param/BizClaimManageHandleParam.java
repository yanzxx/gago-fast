package vip.xiaonuo.biz.modular.claimmanage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 理赔处理参数
 */
@Getter
@Setter
public class BizClaimManageHandleParam {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "理赔单号")
    private String claimNo;

    @ApiModelProperty(value = "处理状态", required = true)
    @NotBlank(message = "status不能为空")
    private String status;

    @ApiModelProperty(value = "结果说明", required = true)
    @NotBlank(message = "resultRemark不能为空")
    private String resultRemark;
}
