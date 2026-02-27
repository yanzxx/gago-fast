package vip.xiaonuo.biz.modular.insproduct.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 保险产品实体
 */
@Getter
@Setter
@TableName("BIZ_INS_PRODUCT")
public class BizInsProduct extends CommonEntity implements TransPojo {

    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    @ApiModelProperty(value = "养殖场id", position = 3)
    private String farmId;

    @ApiModelProperty(value = "产品名称", position = 4)
    private String productName;

    @ApiModelProperty(value = "产品编码", position = 5)
    private String productCode;

    @ApiModelProperty(value = "险种类型", position = 6)
    private String insuranceType;

    @ApiModelProperty(value = "保障范围", position = 7)
    private String coverageScope;

    @ApiModelProperty(value = "费率", position = 8)
    private java.math.BigDecimal premiumRate;

    @ApiModelProperty(value = "最高赔付金额", position = 9)
    private java.math.BigDecimal maxCompensation;

    @ApiModelProperty(value = "状态", position = 10)
    private String status;

    @ApiModelProperty(value = "备注", position = 11)
    private String remark;
}
