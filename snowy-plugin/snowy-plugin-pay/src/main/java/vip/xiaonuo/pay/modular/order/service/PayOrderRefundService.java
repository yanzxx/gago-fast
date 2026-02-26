
package vip.xiaonuo.pay.modular.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.pay.modular.order.entity.PayOrder;
import vip.xiaonuo.pay.modular.order.entity.PayOrderRefund;

import java.util.Date;

/**
 * 订单退款Service接口
 *
 * @author xuyuxiang
 * @date 2023/3/27 15:23
 **/
public interface PayOrderRefundService extends IService<PayOrderRefund> {

    /**
     * 创建退款
     *
     * @author xuyuxiang
     * @date 2023/3/29 22:11
     */
    void doCreateOrderRefund(PayOrder payOrder, String tradeNo, String refundNo, String refundUserId,
                             String refundAccount, String refundAmount, String refundStatus, Date refundTime);

    /**
     * 更新退款信息
     *
     * @author xuyuxiang
     * @date 2023/3/29 22:17
     */
    void updateRefundInfo(String refundNo, String refundStatus, Date refundTime);
}
