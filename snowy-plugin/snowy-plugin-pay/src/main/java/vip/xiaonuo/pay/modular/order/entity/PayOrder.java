
package vip.xiaonuo.pay.modular.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.util.Date;

/**
 * 订单实体
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
@Getter
@Setter
@TableName("PAY_ORDER")
public class PayOrder extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 商户订单号 */
    @ApiModelProperty(value = "商户订单号", position = 3)
    private String outTradeNo;

    /** 支付平台订单号 */
    @ApiModelProperty(value = "支付平台订单号", position = 4)
    private String tradeNo;

    /** 订单标题 */
    @ApiModelProperty(value = "订单标题", position = 5)
    private String subject;

    /** 订单描述 */
    @ApiModelProperty(value = "订单描述", position = 6)
    private String body;

    /** 订单金额 */
    @ApiModelProperty(value = "订单金额", position = 7)
    private String orderAmount;

    /** 买家id */
    @ApiModelProperty(value = "买家id", position = 8)
    private String payUserId;

    /** 买家账号 */
    @ApiModelProperty(value = "买家账号", position = 9)
    private String payAccount;

    /** 支付金额 */
    @ApiModelProperty(value = "支付金额", position = 10)
    private String payAmount;

    /** 支付平台 */
    @ApiModelProperty(value = "支付平台", position = 11)
    private String payPlatform;

    /** 支付状态 */
    @ApiModelProperty(value = "支付状态", position = 12)
    private String payStatus;

    /** 支付时间 */
    @ApiModelProperty(value = "支付时间", position = 13)
    private Date payTime;

    /** 是否有退款 */
    @ApiModelProperty(value = "是否有退款", position = 14)
    private String hasRefund;
}
