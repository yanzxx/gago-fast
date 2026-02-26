
package vip.xiaonuo.pay.modular.wx.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 微信支付OpenId参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 17:52
 */
@Getter
@Setter
public class PayWxJsParam {

    /** openid */
    @ApiModelProperty(value = "openid", required = true, position = 1)
    @NotBlank(message = "openid不能为空")
    private String openid;

    /** 商户订单编号 */
    @ApiModelProperty(value = "商户订单编号", required = true,  position = 2)
    @NotBlank(message = "outTradeNo不能为空")
    private String outTradeNo;
}
