package vip.xiaonuo.biz.modular.afterloan.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 发送整改通知参数
 */
@Getter
@Setter
public class BizAfterLoanSendNoticeParam {

    @ApiModelProperty(value = "异常id", required = true)
    @NotBlank(message = "anomalyId不能为空")
    private String anomalyId;

    @ApiModelProperty(value = "整改要求", required = true)
    @NotBlank(message = "requirement不能为空")
    private String requirement;

    @ApiModelProperty(value = "整改时限", required = true)
    @NotBlank(message = "deadline不能为空")
    private String deadline;

    @ApiModelProperty(value = "通知对象", required = true)
    @NotBlank(message = "receiver不能为空")
    private String receiver;
}
