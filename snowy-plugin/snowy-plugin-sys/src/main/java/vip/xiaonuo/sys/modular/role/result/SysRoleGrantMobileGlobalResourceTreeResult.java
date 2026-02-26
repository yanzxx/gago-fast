
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
public class SysRoleGrantMobileGlobalResourceTreeResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 4)
    private String title;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 5)
    private String code;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 6)
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 7)
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 8)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String extJson;
}
