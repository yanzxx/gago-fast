
package vip.xiaonuo.mobile.modular.resource.param.module;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 移动端模块Id参数
 *
 * @author xuyuxiang
 * @date 2022/7/27 18:40
 **/
@Getter
@Setter
public class MobileModuleIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
