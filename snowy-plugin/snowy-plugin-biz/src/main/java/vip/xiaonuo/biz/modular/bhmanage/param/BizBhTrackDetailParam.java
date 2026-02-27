package vip.xiaonuo.biz.modular.bhmanage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 保后轨迹详情参数
 */
@Getter
@Setter
public class BizBhTrackDetailParam {

    @ApiModelProperty(value = "异常id", required = true)
    @NotBlank(message = "anomalyId不能为空")
    private String anomalyId;
}
