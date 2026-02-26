
package vip.xiaonuo.pay.modular.order.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 订单明细Id参数
 *
 * @author yubaoshan
 * @date  2023/04/09 00:25
 **/
@Getter
@Setter
public class PayOrderDetailsIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
