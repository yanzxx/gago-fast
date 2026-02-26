
package vip.xiaonuo.sys.modular.role.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 角色拥有的移动端菜单结果
 *
 * @author xuyuxiang
 * @date 2022/7/27 15:08
 **/
@Getter
@Setter
public class SysRoleOwnTerminalResult {

    /** 角色id */
    @ApiModelProperty(value = "角色id", position = 1)
    private String id;

    /** 已授权终端信息 */
    @ApiModelProperty(value = "已授权终端信息", position = 2)
    private List<String> grantInfoList;

}
