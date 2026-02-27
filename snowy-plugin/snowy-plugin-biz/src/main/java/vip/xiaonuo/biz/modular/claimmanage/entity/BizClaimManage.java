package vip.xiaonuo.biz.modular.claimmanage.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 理赔管理实体
 */
@Getter
@Setter
@TableName("BIZ_CLAIM_MANAGE")
public class BizClaimManage extends CommonEntity implements TransPojo {

    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    @ApiModelProperty(value = "养殖场id", position = 3)
    private String farmId;

    @ApiModelProperty(value = "理赔单号", position = 4)
    private String claimNo;

    @ApiModelProperty(value = "保单号", position = 5)
    private String policyNo;

    @ApiModelProperty(value = "投保单号", position = 6)
    private String applyNo;

    @ApiModelProperty(value = "被保主体", position = 7)
    private String insuredName;

    @ApiModelProperty(value = "理赔类型", position = 8)
    private String claimType;

    @ApiModelProperty(value = "损失数量", position = 9)
    private Integer lossCount;

    @ApiModelProperty(value = "理赔金额", position = 10)
    private java.math.BigDecimal claimAmount;

    @ApiModelProperty(value = "出险时间", position = 11)
    private String occurTime;

    @ApiModelProperty(value = "状态", position = 12)
    private String status;

    @ApiModelProperty(value = "附件材料", position = 13)
    private String evidenceFiles;

    @ApiModelProperty(value = "结果说明", position = 14)
    private String resultRemark;

    @ApiModelProperty(value = "备注", position = 15)
    private String remark;
}
