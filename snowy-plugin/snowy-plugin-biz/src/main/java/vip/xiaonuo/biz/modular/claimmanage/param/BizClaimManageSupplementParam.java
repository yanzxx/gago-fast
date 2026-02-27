package vip.xiaonuo.biz.modular.claimmanage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 理赔补充材料参数
 */
@Getter
@Setter
public class BizClaimManageSupplementParam {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "理赔单号")
    private String claimNo;

    @ApiModelProperty(value = "补充附件", required = true)
    @NotBlank(message = "files不能为空")
    private String files;

    @ApiModelProperty(value = "补充说明", required = true)
    @NotBlank(message = "remark不能为空")
    private String remark;
}
