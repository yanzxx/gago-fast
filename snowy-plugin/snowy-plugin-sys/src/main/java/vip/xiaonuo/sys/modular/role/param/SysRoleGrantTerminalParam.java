
package vip.xiaonuo.sys.modular.role.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色授权资源参数
 *
 * @author xuyuxiang
 * @date 2022/7/27 15:05
 **/
@Getter
@Setter
public class SysRoleGrantTerminalParam {

    /** 角色id */
    @ApiModelProperty(value = "角色id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 授权终端信息 */
    @ApiModelProperty(value = "授权终端信息", required = true, position = 2)
    @NotEmpty(message = "grantTerminalList 不能为空")
    private List<String> grantTerminalList;

}
