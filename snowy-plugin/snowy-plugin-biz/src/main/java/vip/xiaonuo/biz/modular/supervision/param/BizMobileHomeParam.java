package vip.xiaonuo.biz.modular.supervision.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端首页参数
 */
@Getter
@Setter
public class BizMobileHomeParam {

    @ApiModelProperty("养殖场id")
    private String farmId;
}
