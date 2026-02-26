
package vip.xiaonuo.pay.modular.order.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.pay.modular.order.entity.PayOrder;
import vip.xiaonuo.pay.modular.order.param.PayOrderCreateParam;
import vip.xiaonuo.pay.modular.order.service.PayOrderService;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 订单示例控制器
 *
 * @author yubaoshan
 * @date 2022/2/23 18:26
 **/
@Api(tags = "订单示例控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 4)
@RestController
@Validated
public class PayOrderSampleController {

    @Resource
    private PayOrderService payOrderService;

    /**
     * 创建订单
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("创建订单")
    @PostMapping("/pay/order/sample/doCreateOrder")
    public CommonResult<PayOrder> doCreateOrder(@Valid @RequestBody PayOrderCreateParam payOrderCreateParam) {
        PayOrder payOrder = payOrderService.createOrder(payOrderCreateParam);
        return CommonResult.data(payOrder);
    }
}
