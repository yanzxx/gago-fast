
package vip.xiaonuo.pay.modular.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.pay.modular.order.entity.PayOrder;
import vip.xiaonuo.pay.modular.order.entity.PayOrderRefund;
import vip.xiaonuo.pay.modular.order.mapper.PayOrderRefundMapper;
import vip.xiaonuo.pay.modular.order.service.PayOrderRefundService;

import java.util.Date;

/**
 * 订单Service接口实现类
 *
 * @author xuyuxiang
 * @date 2023/3/27 15:24
 **/
@Service
public class PayOrderRefundServiceImpl extends ServiceImpl<PayOrderRefundMapper, PayOrderRefund> implements PayOrderRefundService {

    @Override
    public void doCreateOrderRefund(PayOrder payOrder, String tradeNo, String refundNo, String refundUserId,
                                    String refundAccount, String refundAmount, String refundStatus, Date refundTime) {
        PayOrderRefund payOrderRefund = new PayOrderRefund();
        payOrderRefund.setOrderId(payOrder.getId());
        payOrderRefund.setOutTradeNo(payOrder.getOutTradeNo());
        payOrderRefund.setTradeNo(tradeNo);
        payOrderRefund.setRefundNo(refundNo);
        payOrderRefund.setRefundUserId(refundUserId);
        payOrderRefund.setRefundAccount(refundAccount);
        payOrderRefund.setRefundAmount(refundAmount);
        payOrderRefund.setRefundStatus(refundStatus);
        payOrderRefund.setRefundTime(refundTime);
        this.save(payOrderRefund);
    }

    @Override
    public void updateRefundInfo(String refundNo, String refundStatus, Date refundTime) {
        this.update(new LambdaUpdateWrapper<PayOrderRefund>().eq(PayOrderRefund::getRefundNo, refundNo)
                .set(PayOrderRefund::getRefundStatus, refundStatus).set(PayOrderRefund::getRefundTime, refundTime));
    }
}
