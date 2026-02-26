
package vip.xiaonuo.pay.modular.order.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 创建订单参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 17:52
 */
@Getter
@Setter
public class PayOrderCreateParam {

    /** 支付平台 */
    @ApiModelProperty(value = "payPlatform", required = true)
    @NotBlank(message = "payPlatform不能为空")
    private String payPlatform;
}
