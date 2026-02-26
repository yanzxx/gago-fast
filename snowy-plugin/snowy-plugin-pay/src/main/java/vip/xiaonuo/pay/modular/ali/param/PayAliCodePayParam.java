
package vip.xiaonuo.pay.modular.ali.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 支付宝付款码支付参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 17:52
 */
@Getter
@Setter
public class PayAliCodePayParam {

    /** 付款码 */
    @ApiModelProperty(value = "付款码", required = true, position = 1)
    @NotBlank(message = "authCode不能为空")
    private String authCode;

    /** 商户订单编号 */
    @ApiModelProperty(value = "商户订单编号", required = true, position = 2)
    @NotBlank(message = "outTradeNo不能为空")
    private String outTradeNo;
}
