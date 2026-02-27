package vip.xiaonuo.biz.modular.afterloan.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 贷后整改通知实体
 */
@Getter
@Setter
@TableName("BIZ_AFTER_LOAN_NOTICE")
public class BizAfterLoanNotice extends CommonEntity implements TransPojo {

    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    @ApiModelProperty(value = "异常id", position = 3)
    private String anomalyId;

    @ApiModelProperty(value = "养殖场id", position = 4)
    private String farmId;

    @ApiModelProperty(value = "整改要求", position = 5)
    private String requirement;

    @ApiModelProperty(value = "整改截止日期", position = 6)
    private String deadline;

    @ApiModelProperty(value = "通知对象", position = 7)
    private String receiver;

    @ApiModelProperty(value = "发送状态", position = 8)
    private String sendStatus;
}
