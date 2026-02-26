
package vip.xiaonuo.pay.modular.order.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 订单支付平台枚举
 *
 * @author xuyuxiang
 * @date 2021/10/11 14:02
 **/
@Getter
public enum PayOrderPayPlatformEnum {

    /**
     * 支付宝
     */
    ALIPAY("ALIPAY"),

    /**
     * 微信支付
     */
    WXPAY("WXPAY");

    private final String value;

    PayOrderPayPlatformEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = ALIPAY.getValue().equals(value) || WXPAY.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的支付平台：{}", value);
        }
    }
}
