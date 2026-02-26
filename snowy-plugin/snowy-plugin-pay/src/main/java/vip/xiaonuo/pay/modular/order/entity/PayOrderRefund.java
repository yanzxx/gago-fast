
package vip.xiaonuo.pay.modular.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.util.Date;

/**
 * 订单退款实体
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
@Getter
@Setter
@TableName("PAY_ORDER_REFUND")
public class PayOrderRefund extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 订单id */
    @ApiModelProperty(value = "订单id", position = 3)
    private String orderId;

    /** 商户订单号 */
    @ApiModelProperty(value = "商户订单号", position = 4)
    private String outTradeNo;

    /** 支付平台退款单号 */
    @ApiModelProperty(value = "支付平台退款单号", position = 5)
    private String tradeNo;

    /** 商户退款单号 */
    @ApiModelProperty(value = "商户退款单号", position = 6)
    private String refundNo;

    /** 退款到买家id */
    @ApiModelProperty(value = "退款到买家id", position = 7)
    private String refundUserId;

    /** 退款到买家账号 */
    @ApiModelProperty(value = "退款到买家账号", position = 8)
    private String refundAccount;

    /** 退款金额 */
    @ApiModelProperty(value = "退款金额", position = 9)
    private String refundAmount;

    /** 退款状态 */
    @ApiModelProperty(value = "退款状态", position = 10)
    private String refundStatus;

    /** 退款时间 */
    @ApiModelProperty(value = "退款时间", position = 11)
    private Date refundTime;
}
