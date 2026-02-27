package vip.xiaonuo.biz.modular.livestock.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 农场畜种选项参数
 */
@Getter
@Setter
public class BizLivestockSpeciesOptionParam {

    @ApiModelProperty(value = "农场id")
    private String farmId;
}
