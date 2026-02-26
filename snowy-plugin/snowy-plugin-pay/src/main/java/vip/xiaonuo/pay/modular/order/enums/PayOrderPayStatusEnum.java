
package vip.xiaonuo.pay.modular.order.enums;

import lombok.Getter;

/**
 * 订单支付状态枚举，此枚举仅记录了最终状态
 *
 * @author xuyuxiang
 * @date 2021/10/11 14:02
 **/
@Getter
public enum PayOrderPayStatusEnum {

    /**
     * 已下单：只是下单了商品，还没创建交易
     */
    NO_TRADE("NO_TRADE"),
    /**
     * 交易关闭：未付款交易超时关闭，未付款主动关闭，或支付完成后全额退款
     */
    TRADE_CLOSED("TRADE_CLOSED");

    private final String value;

    PayOrderPayStatusEnum(String value) {
        this.value = value;
    }
}
