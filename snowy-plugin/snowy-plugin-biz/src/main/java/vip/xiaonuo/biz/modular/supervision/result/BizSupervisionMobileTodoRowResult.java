package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端首页待办原始项
 */
@Getter
@Setter
public class BizSupervisionMobileTodoRowResult {

    @ApiModelProperty("异常id")
    private String id;

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("待办类型编码")
    private String todoType;

    @ApiModelProperty("待办类型名称")
    private String todoName;

    @ApiModelProperty("异常类型")
    private String anomalyType;

    @ApiModelProperty("触发时间")
    private String triggerTime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("内容摘要")
    private String content;
}
