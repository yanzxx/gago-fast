
package vip.xiaonuo.dbs.modular.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 数据源实体
 *
 * @author xuyuxiang
 * @date 2022/3/9 9:13
 **/
@Getter
@Setter
@TableName("EXT_DATABASE")
public class DbsStorage extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 3)
    private String poolName;

    /** 连接URL */
    @ApiModelProperty(value = "连接URL", position = 4)
    private String url;

    /** 用户名 */
    @ApiModelProperty(value = "用户名", position = 5)
    private String username;

    /** 密码 */
    @ApiModelProperty(value = "密码", position = 6)
    private String password;

    /** 驱动名称 */
    @ApiModelProperty(value = "驱动名称", position = 7)
    private String driverName;

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
