package vip.xiaonuo.biz.modular.loan.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 贷款申请实体
 */
@Getter
@Setter
@TableName("BIZ_LOAN_APPLY")
public class BizLoanApply extends CommonEntity implements TransPojo {

    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    @ApiModelProperty(value = "养殖场id", position = 3)
    private String farmId;

    @ApiModelProperty(value = "申请单号", position = 4)
    private String applyNo;

    @ApiModelProperty(value = "产品编码", position = 5)
    private String productCode;

    @ApiModelProperty(value = "申请金额", position = 6)
    private java.math.BigDecimal applyAmount;

    @ApiModelProperty(value = "申请人", position = 7)
    private String applicantName;

    @ApiModelProperty(value = "状态", position = 8)
    private String loanStatus;

    @ApiModelProperty(value = "备注", position = 9)
    private String remark;

    @ApiModelProperty(value = "申请时间", position = 10)
    private java.util.Date applyTime;
}
