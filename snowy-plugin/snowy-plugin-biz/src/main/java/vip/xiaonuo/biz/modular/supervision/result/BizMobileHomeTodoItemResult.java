package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端首页待办明细项
 */
@Getter
@Setter
public class BizMobileHomeTodoItemResult {

    @ApiModelProperty("异常id")
    private String id;

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("异常类型")
    private String anomalyType;

    @ApiModelProperty("触发时间")
    private String triggerTime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("摘要")
    private String content;
}
