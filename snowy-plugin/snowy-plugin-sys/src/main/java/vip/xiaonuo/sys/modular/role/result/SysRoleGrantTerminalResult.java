
package vip.xiaonuo.sys.modular.role.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 角色授权移动端菜单树结果
 *
 * @author xuyuxiang
 * @date 2022/7/27 15:09
 **/
@Getter
@Setter
public class SysRoleGrantTerminalResult {

    /** 终端 */
    @ApiModelProperty(value = "终端", position = 1)
    private String value;

    /** 终端描述*/
    @ApiModelProperty(value = "终端描述", position = 2)
    private String desc;
}
