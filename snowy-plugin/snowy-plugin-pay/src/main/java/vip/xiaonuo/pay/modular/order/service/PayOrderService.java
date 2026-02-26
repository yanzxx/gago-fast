
package vip.xiaonuo.pay.modular.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.pay.modular.order.entity.PayOrder;
import vip.xiaonuo.pay.modular.order.entity.PayOrderRefund;
import vip.xiaonuo.pay.modular.order.param.PayOrderCreateParam;
import vip.xiaonuo.pay.modular.order.param.PayOrderIdParam;
import vip.xiaonuo.pay.modular.order.param.PayOrderPageParam;
import vip.xiaonuo.pay.modular.order.param.PayOrderRefundParam;

import java.util.List;

/**
 * 订单Service接口
 *
 * @author xuyuxiang
 * @date 2023/3/27 15:23
 **/
public interface PayOrderService extends IService<PayOrder> {

    /**
     * 创建订单
     *
     * @author xuyuxiang
     * @date 2023/4/10 13:25
     **/
    PayOrder createOrder(PayOrderCreateParam payOrderCreateParam);

    /**
     * 获取订单分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<PayOrder> page(PayOrderPageParam payOrderPageParam);

    /**
     * 获取退款列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<PayOrderRefund> refundList(PayOrderIdParam payOrderIdParam);

    /**
     * 执行退款
     *
     * @author xuyuxiang
     * @date 2023/3/28 13:18
     **/
    void doRefund(PayOrderRefundParam payOrderRefundParam);

    /**
     * 删除订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<PayOrderIdParam> payOrderIdParamList);

    /**
     * 获取配置详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    PayOrder queryEntity(String id);

    /**
     * 同步订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void sync(List<PayOrderIdParam> payOrderIdParamList);

    /**
     * 关闭订单
     *
     * @author xuyuxiang
     * @date 2023/3/30 9:29
     **/
    void close(List<PayOrderIdParam> payOrderIdParamList);
}
