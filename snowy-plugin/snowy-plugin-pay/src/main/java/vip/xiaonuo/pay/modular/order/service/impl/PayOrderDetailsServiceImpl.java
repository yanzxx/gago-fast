
package vip.xiaonuo.pay.modular.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.pay.modular.order.entity.PayOrderDetails;
import vip.xiaonuo.pay.modular.order.mapper.PayOrderDetailsMapper;
import vip.xiaonuo.pay.modular.order.service.PayOrderDetailsService;

import java.util.List;

/**
 * 订单明细Service接口实现类
 *
 * @author yubaoshan
 * @date  2023/04/09 00:25
 **/
@Service
public class PayOrderDetailsServiceImpl extends ServiceImpl<PayOrderDetailsMapper, PayOrderDetails> implements PayOrderDetailsService {

    @Override
    public List<PayOrderDetails> orderDetailsList(String orderId) {
        return this.list(new LambdaQueryWrapper<PayOrderDetails>().eq(PayOrderDetails::getOrderId, orderId));
    }
}
