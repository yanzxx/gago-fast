
package vip.xiaonuo.pay.modular.order.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单查询参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 17:53
 */
@Getter
@Setter
public class PayOrderPageParam {

    /** 当前页 */
    @ApiModelProperty(value = "当前页码")
    private Integer current;

    /** 每页条数 */
    @ApiModelProperty(value = "每页条数")
    private Integer size;

    /** 排序字段 */
    @ApiModelProperty(value = "排序字段，字段驼峰名称，如：userName")
    private String sortField;

    /** 排序方式 */
    @ApiModelProperty(value = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;

    /** 订单关键词 */
    @ApiModelProperty(value = "订单关键词")
    private String searchKey;

    /** 支付平台 */
    @ApiModelProperty(value = "支付平台")
    private String payPlatform;

    /** 支付状态 */
    @ApiModelProperty(value = "支付状态")
    private String payStatus;

    /** 是否有退款 */
    @ApiModelProperty(value = "是否有退款")
    private String hasRefund;

    /** 支付开始时间 */
    @ApiModelProperty(value = "支付开始时间")
    private String startPayTime;

    /** 支付结束时间 */
    @ApiModelProperty(value = "支付结束时间")
    private String endPayTime;

    /** 创建开始时间 */
    @ApiModelProperty(value = "创建开始时间")
    private String startCreateTime;

    /** 创建结束时间 */
    @ApiModelProperty(value = "创建结束时间")
    private String endCreateTime;
}
