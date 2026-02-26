
package vip.xiaonuo.pay.modular.order.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 订单退款参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 17:52
 */
@Getter
@Setter
public class PayOrderRefundParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 退款金额 */
    @ApiModelProperty(value = "退款金额", required = true)
    @NotBlank(message = "refundAmount不能为空")
    private String refundAmount;
}
