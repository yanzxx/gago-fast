
package vip.xiaonuo.pay.modular.order.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 支付宝订单退款状态枚举
 *
 * @author xuyuxiang
 * @date 2021/10/11 14:02
 **/
@Getter
public enum PayOrderRefundStatusEnum {

    /**
     * 退款处理中
     */
    REFUND_PENDING("REFUND_PENDING"),

    /**
     * 退款成功
     */
    REFUND_SUCCESS("REFUND_SUCCESS"),

    /**
     * 退款失败
     */
    REFUND_FAIL("REFUND_FAIL");

    private final String value;

    PayOrderRefundStatusEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = REFUND_PENDING.getValue().equals(value) || REFUND_SUCCESS.getValue().equals(value) || REFUND_FAIL.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的退款状态：{}", value);
        }
    }
}
