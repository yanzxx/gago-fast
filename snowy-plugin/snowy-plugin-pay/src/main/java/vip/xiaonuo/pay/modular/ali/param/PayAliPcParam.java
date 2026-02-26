
package vip.xiaonuo.pay.modular.ali.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 支付宝PC支付参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 17:52
 */
@Getter
@Setter
public class PayAliPcParam {

    /** 商户订单编号 */
    @ApiModelProperty(value = "商户订单编号", required = true)
    @NotBlank(message = "outTradeNo不能为空")
    private String outTradeNo;
}
