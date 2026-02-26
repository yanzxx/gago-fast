
package vip.xiaonuo.pay.modular.ali.service;

import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import vip.xiaonuo.pay.modular.ali.param.PayAliCodePayParam;
import vip.xiaonuo.pay.modular.ali.param.PayAliPcParam;
import vip.xiaonuo.pay.modular.ali.param.PayAliQrParam;
import vip.xiaonuo.pay.modular.ali.param.PayAliWapParam;
import vip.xiaonuo.pay.modular.order.entity.PayOrder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付宝Service接口
 *
 * @author xuyuxiang
 * @date 2023/3/21 16:14
 **/
public interface PayAliService {

    /**
     * 支付回调通知
     *
     * @author xuyuxiang
     * @date 2023/3/27 14:25
     **/
    String notifyUrl(HttpServletRequest request);

    /**
     * 商家账户余额查询
     *
     * @author xuyuxiang
     * @date 2023/3/30 13:18
     **/
    String accountQuery();

    /**
     * 支付宝付款码支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    void codePay(PayAliCodePayParam payAliCodePayParam);

    /**
     * 支付宝扫码支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    String qrPay(PayAliQrParam payAliQrParam);

    /**
     * 支付宝PC支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    String pcPay(PayAliPcParam payAliPcParam);

    /**
     * 支付宝WAP支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:21
     **/
    String wapPay(PayAliWapParam payAliWapParam);

    /**
     * 交易查询
     *
     * @author xuyuxiang
     * @date 2023/3/28 14:19
     **/
    AlipayTradeQueryResponse tradeQuery(String outTradeNo);

    /**
     * 退款查询
     *
     * @author xuyuxiang
     * @date 2023/3/28 14:19
     **/
    AlipayTradeFastpayRefundQueryResponse refundQuery(String outTradeNo, String outRequestNo);

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
    void doSyncForNotify(PayOrder payOrder, Map<String, String> params);

    /**
     * 执行同步，用于主动查询
     *
     * @author xuyuxiang
     * @date 2023/3/29 19:48
     */
    void doSyncForTradeQuery(PayOrder payOrder);
}
