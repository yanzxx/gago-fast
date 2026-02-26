
package vip.xiaonuo.pay.core.wx;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

/**
 * 微信支付Api封装
 *
 * @author xuyuxiang
 * @date 2023/3/31 15:53
 **/
public class WxPayApi {

    /**
     * 微信公众号AppSecret（用于JSAPI支付）
     */
    public static String WX_WP_APP_SECRET;

    /**
     * 退款回调地址
     */
    public static String REFUND_NOTIFY_URL;

    /**
     * 获取微信支付Api
     *
     * @author xuyuxiang
     * @date 2023/3/31 16:22
     **/
    public static WxPayService me() {
        WxPayConfig wxPayConfig = WxPayApiConfigKit.getWxPayConfig();
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        return wxPayService;
    }
}
