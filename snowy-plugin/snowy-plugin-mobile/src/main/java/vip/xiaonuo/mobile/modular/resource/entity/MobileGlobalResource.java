
package vip.xiaonuo.mobile.modular.resource.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 全局按钮实体
 *
 * @author xuyuxiang
 * @date 2022/7/27 18:25
 **/
@Getter
@Setter
@TableName("MOBILE_GLOBAL_RESOURCE")
public class MobileGlobalResource extends CommonEntity {

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
