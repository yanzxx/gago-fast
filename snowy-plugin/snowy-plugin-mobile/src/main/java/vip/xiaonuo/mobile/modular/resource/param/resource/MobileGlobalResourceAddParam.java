
package vip.xiaonuo.mobile.modular.resource.param.resource;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端按钮查询参数
 *
 * @author xuyuxiang
 * @date 2022/7/27 18:40
 **/
@Getter
@Setter
public class MobileGlobalResourceAddParam {

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
