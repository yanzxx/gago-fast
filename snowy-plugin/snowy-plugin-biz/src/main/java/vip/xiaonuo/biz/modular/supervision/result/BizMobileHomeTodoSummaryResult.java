package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 移动端首页待办聚合
 */
@Getter
@Setter
public class BizMobileHomeTodoSummaryResult {

    @ApiModelProperty("待办类型编码")
    private String todoType;

    @ApiModelProperty("待办类型名称")
    private String todoName;

    @ApiModelProperty("待办数量")
    private Long count = 0L;

    @ApiModelProperty("前N条摘要")
    private List<BizMobileHomeTodoItemResult> items = new ArrayList<>();
}
