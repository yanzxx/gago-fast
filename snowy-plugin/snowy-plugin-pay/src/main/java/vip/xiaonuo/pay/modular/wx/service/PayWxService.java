
package vip.xiaonuo.pay.modular.wx.service;

import cn.hutool.json.JSONObject;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import vip.xiaonuo.pay.modular.order.entity.PayOrder;
import vip.xiaonuo.pay.modular.wx.param.PayWxCodePayParam;
import vip.xiaonuo.pay.modular.wx.param.PayWxH5Param;
import vip.xiaonuo.pay.modular.wx.param.PayWxJsParam;
import vip.xiaonuo.pay.modular.wx.param.PayWxQrParam;

import java.io.IOException;

/**
 * 微信支付Service接口
 *
 * @author xuyuxiang
 * @date 2023/3/28 14:12
 **/
public interface PayWxService {

    /**
     * 支付回调通知
     *
     * @author xuyuxiang
     * @date 2023/3/27 14:25
     **/
    String notifyUrl(String notifyData);

    /**
     * 退款回调通知
     *
     * @author xuyuxiang
     * @date 2023/3/27 14:25
     **/
    String refundNotifyUrl(String notifyData);

    /**
     * 获取授权地址
     *
     * @author xuyuxiang
     * @date 2023/4/4 15:46
     **/
    String authUrl();

    /**
     * 授权回调通知
     *
     * @author xuyuxiang
     * @date 2023/3/27 14:25
     **/
    void authNotifyUrl(String code) throws IOException;

    /**
     * 商家账户余额查询
     *
     * @author xuyuxiang
     * @date 2023/3/30 13:18
     **/
    String accountQuery();

    /**
     * 微信付款码支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    void codePay(PayWxCodePayParam payWxCodePayParam);

    /**
     * 微信扫码支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    String qrPay(PayWxQrParam payWxQrParam);

    /**
     * 微信JSAPI支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    JSONObject jsPay(PayWxJsParam payWxJsParam);

    /**
     * 微信H5支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:21
     **/
    String h5Pay(PayWxH5Param payWxH5Param);

    /**
     * 交易查询
     *
     * @author xuyuxiang
     * @date 2023/3/28 14:19
     **/
    WxPayOrderQueryV3Result tradeQuery(String outTradeNo);

    /**
     * 退款查询
     *
     * @author xuyuxiang
     * @date 2023/3/28 14:19
     **/
    WxPayRefundQueryV3Result refundQuery(String outTradeNo, String outRequestNo);

    /**
     * 执行退款
     *
     * @author xuyuxiang
     * @date 2023/3/29 21:40
     */
    void doRefund(PayOrder payOrder, String refundAmount);

    /**
     * 执行关闭
     *
     * @author xuyuxiang
     * @date 2023/3/29 21:40
     */
    void doClose(PayOrder payOrder);

    /**
     * 执行同步，用于异步通知
     *
     * @author xuyuxiang
     * @date 2023/3/29 19:48
     */
    void doSyncForNotify(PayOrder payOrder, WxPayOrderNotifyV3Result wxPayOrderNotifyV3Result);

    /**
     * 执行同步，用于主动查询
     *
     * @author xuyuxiang
     * @date 2023/3/29 19:48
     */
    void doSyncForTradeQuery(PayOrder payOrder);
}
