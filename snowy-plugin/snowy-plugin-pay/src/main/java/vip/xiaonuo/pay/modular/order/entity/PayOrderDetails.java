
package vip.xiaonuo.pay.modular.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 订单明细实体
 *
 * @author yubaoshan
 * @date  2023/04/09 00:25
 **/
@Getter
@Setter
@TableName("PAY_ORDER_DETAILS")
public class PayOrderDetails extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 订单id */
    @ApiModelProperty(value = "订单id", position = 3)
    private String orderId;

    /** 产品id */
    @ApiModelProperty(value = "产品id", position = 4)
    private String productId;

    /** 产品名称 */
    @ApiModelProperty(value = "产品名称", position = 5)
    private String productName;

    /** 产品价格 */
    @ApiModelProperty(value = "产品价格", position = 6)
    private String productAmount;

    /** 产品数量 */
    @ApiModelProperty(value = "产品数量", position = 7)
    private Integer productCount;
}
