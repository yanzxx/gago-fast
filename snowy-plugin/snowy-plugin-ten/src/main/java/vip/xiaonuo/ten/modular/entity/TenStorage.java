
package vip.xiaonuo.ten.modular.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 多租户实体
 *
 * @author xuyuxiang
 * @date 2022/3/10 19:55
 **/
@Getter
@Setter
@TableName("EXT_TENANT")
public class TenStorage extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 数据源id */
    @ApiModelProperty(value = "数据源id", position = 3)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String dbsId;

    /** 数据源名称 */
    @ApiModelProperty(value = "数据源名称", position = 4)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String dbsName;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 5)
    private String name;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 6)
    private String code;

    /** 绑定域名 */
    @ApiModelProperty(value = "绑定域名", position = 7)
    private String domain;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 8)
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 9)
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 10)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String extJson;
}
