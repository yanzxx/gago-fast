package vip.xiaonuo.biz.modular.insapply.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 投保记录实体
 */
@Getter
@Setter
@TableName("BIZ_INS_APPLY")
public class BizInsApply extends CommonEntity implements TransPojo {

    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    @ApiModelProperty(value = "养殖场id", position = 3)
    private String farmId;

    @ApiModelProperty(value = "投保单号", position = 4)
    private String applyNo;

    @ApiModelProperty(value = "保单号", position = 5)
    private String policyNo;

    @ApiModelProperty(value = "保险产品编码", position = 6)
    private String productCode;

    @ApiModelProperty(value = "被保主体", position = 7)
    private String insuredName;

    @ApiModelProperty(value = "投保畜种", position = 8)
    private String speciesType;

    @ApiModelProperty(value = "投保数量", position = 9)
    private Integer insuredCount;

    @ApiModelProperty(value = "投保金额", position = 10)
    private java.math.BigDecimal insuredAmount;

    @ApiModelProperty(value = "保费金额", position = 11)
    private java.math.BigDecimal premiumAmount;

    @ApiModelProperty(value = "状态", position = 12)
    private String status;

    @ApiModelProperty(value = "备注", position = 13)
    private String remark;
}
