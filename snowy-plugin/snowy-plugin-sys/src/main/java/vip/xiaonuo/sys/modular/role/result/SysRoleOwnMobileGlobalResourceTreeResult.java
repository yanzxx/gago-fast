
package vip.xiaonuo.sys.modular.role.result;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class SysRoleOwnMobileGlobalResourceTreeResult {

    /** 角色id */
    @ApiModelProperty(value = "角色id", position = 1)
    private String id;

    /** 已授权移动端全局权限 */
    @ApiModelProperty(value = "已授权移动端全局权限", position = 2)
    private List<String> grantGlobalList;

}
