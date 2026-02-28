package vip.xiaonuo.biz.modular.supervision.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 综合监管首页参数
 */
@Getter
@Setter
public class BizSupervisionHomeParam {

    @ApiModelProperty("养殖场id")
    private String farmId;
}
