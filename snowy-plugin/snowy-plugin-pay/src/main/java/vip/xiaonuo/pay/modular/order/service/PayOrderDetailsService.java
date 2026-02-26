
package vip.xiaonuo.pay.modular.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.pay.modular.order.entity.PayOrderDetails;

import java.util.List;

/**
 * 订单明细Service接口
 *
 * @author yubaoshan
 * @date  2023/04/09 00:25
 **/
public interface PayOrderDetailsService extends IService<PayOrderDetails> {

    /**
     * 获取订单明细列表
     *
     * @author yubaoshan
     * @date  2023/04/09 00:25
     */
    List<PayOrderDetails> orderDetailsList(String orderId);
}
