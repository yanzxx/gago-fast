package vip.xiaonuo.biz.modular.afterloan.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 贷后异常实体
 */
@Getter
@Setter
@TableName("BIZ_AFTER_LOAN_ANOMALY")
public class BizAfterLoanAnomaly extends CommonEntity implements TransPojo {

    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    @ApiModelProperty(value = "养殖场id", position = 3)
    private String farmId;

    @ApiModelProperty(value = "异常类型", position = 4)
    private String anomalyType;

    @ApiModelProperty(value = "异常对象", position = 5)
    private String targetNo;

    @ApiModelProperty(value = "触发时间", position = 6)
    private java.util.Date triggerTime;

    @ApiModelProperty(value = "风险等级", position = 7)
    private String riskLevel;

    @ApiModelProperty(value = "状态", position = 8)
    private String status;

    @ApiModelProperty(value = "异常描述", position = 9)
    private String description;
}
