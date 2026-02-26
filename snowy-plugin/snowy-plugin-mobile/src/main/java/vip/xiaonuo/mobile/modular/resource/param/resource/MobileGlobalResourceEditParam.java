
package vip.xiaonuo.mobile.modular.resource.param.resource;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 移动端按钮查询参数
 *
 * @author xuyuxiang
 * @date 2022/7/27 18:40
 **/
@Getter
@Setter
public class MobileGlobalResourceEditParam {

    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 4)
    @NotBlank(message = "title不能为空")
    private String title;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 5)
    @NotBlank(message = "code不能为空")
    private String code;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 6)
    @NotBlank(message = "category不能为空")
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 7)
    @NotBlank(message = "sortCode不能为空")
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 8)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String extJson;
}
