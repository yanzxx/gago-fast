
package vip.xiaonuo.pay.modular.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.pojo.CommonValidList;
import vip.xiaonuo.pay.modular.order.entity.PayOrder;
import vip.xiaonuo.pay.modular.order.entity.PayOrderDetails;
import vip.xiaonuo.pay.modular.order.entity.PayOrderRefund;
import vip.xiaonuo.pay.modular.order.param.PayOrderIdParam;
import vip.xiaonuo.pay.modular.order.param.PayOrderPageParam;
import vip.xiaonuo.pay.modular.order.param.PayOrderRefundParam;
import vip.xiaonuo.pay.modular.order.service.PayOrderDetailsService;
import vip.xiaonuo.pay.modular.order.service.PayOrderService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 订单控制器
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:26
 **/
@Api(tags = "订单控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 3)
@RestController
@Validated
public class PayOrderController {

    @Resource
    private PayOrderService payOrderService;

    @Resource
    private PayOrderDetailsService payOrderDetailsService;

    /**
     * 获取订单分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取订单分页")
    @GetMapping("/pay/order/page")
    public CommonResult<Page<PayOrder>> page(PayOrderPageParam payOrderPageParam) {
        return CommonResult.data(payOrderService.page(payOrderPageParam));
    }

    /**
     * 获取订单明细列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("获取订单明细列表")
    @GetMapping("/pay/order/detailsList")
    public CommonResult<List<PayOrderDetails>> detailsList(@Valid PayOrderIdParam payOrderIdParam) {
        return CommonResult.data(payOrderDetailsService.orderDetailsList(payOrderIdParam.getId()));
    }

    /**
     * 获取退款列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("获取退款列表")
    @GetMapping("/pay/order/refundList")
    public CommonResult<List<PayOrderRefund>> refundList(@Valid PayOrderIdParam payOrderIdParam) {
        return CommonResult.data(payOrderService.refundList(payOrderIdParam));
    }

    /**
     * 执行退款
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("执行退款")
    @PostMapping("/pay/order/doRefund")
    public CommonResult<String> doRefund(@RequestBody @Valid PayOrderRefundParam payOrderRefundParam) {
        payOrderService.doRefund(payOrderRefundParam);
        return CommonResult.ok();
    }

    /**
     * 删除订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("删除订单")
    @CommonLog("删除订单")
    @PostMapping("/pay/order/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<PayOrderIdParam> payOrderIdParamList) {
        payOrderService.delete(payOrderIdParamList);
        return CommonResult.ok();
    }

    /**
     * 同步订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("同步订单")
    @CommonLog("同步订单")
    @PostMapping("/pay/order/sync")
    public CommonResult<String> sync(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<PayOrderIdParam> payOrderIdParamList) {
        payOrderService.sync(payOrderIdParamList);
        return CommonResult.ok();
    }

    /**
     * 关闭订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("关闭订单")
    @CommonLog("关闭订单")
    @PostMapping("/pay/order/close")
    public CommonResult<String> close(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                  CommonValidList<PayOrderIdParam> payOrderIdParamList) {
        payOrderService.close(payOrderIdParamList);
        return CommonResult.ok();
    }
}
