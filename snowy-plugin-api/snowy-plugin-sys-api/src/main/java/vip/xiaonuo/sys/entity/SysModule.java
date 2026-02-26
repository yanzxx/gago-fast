
package vip.xiaonuo.sys.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import javax.validation.constraints.NotBlank;

/**
 * 模块实体
 *
 * @author xuyuxiang
 * @date 2022/7/27 18:20
 **/
@Getter
@Setter
@TableName("SYS_RESOURCE")
public class SysModule extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 3)
    private String title;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 4)
    private String code;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 5)
    private String category;

    /** 图标 */
    @ApiModelProperty(value = "图标", position = 6)
    private String icon;

    /** 颜色 */
    @ApiModelProperty(value = "颜色", position = 7)
    private String color;


    /** 别名 */
    @ApiModelProperty(value = "别名", position = 8)
    private String name;

    /** 组件 */
    @ApiModelProperty(value = "组件", position = 9)
    private String component;

    /** 路径 */
    @ApiModelProperty(value = "路径", position = 10)
    private String path;


    /** 菜单类型 */
    @ApiModelProperty(value = "菜单类型", required = true, position = 11)
    @NotBlank(message = "menuType不能为空")
    private String menuType;


    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 12)
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 13)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String extJson;
}
