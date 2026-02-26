
package vip.xiaonuo.mobile.modular.resource.param.menu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 移动端菜单Id参数
 *
 * @author yubaoshan
 * @date  2023/01/28 22:42
 **/
@Getter
@Setter
public class MobileMenuIdParam {

    /** 主键 */
    @ApiModelProperty(value = "主键", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
