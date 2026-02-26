
package vip.xiaonuo.sys.modular.region.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import vip.xiaonuo.common.annotation.ExcelConfig;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 行政区划实体
 *
 * @author gago
 * @date  2025/08/26 15:08
 **/
@Getter
@Setter
@TableName("t_region")
public class Region {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 区划代码 */
    @ApiModelProperty(value = "区划代码", position = 2)
    private String code;

    /** 区划名称 */
    @ApiModelProperty(value = "区划名称", position = 3)
    private String name;

    /** 父级区划代码 */
    @ApiModelProperty(value = "父级区划代码", position = 4)
    private String parentCode;

    /** 父级区划名称 */
    @ApiModelProperty(value = "父级区划名称", position = 5)
    private String parentName;

    /** GEOJSON */
    @ApiModelProperty(value = "GEOJSON", position = 6)
    @JsonIgnore
    @TableField(exist = false)
    private String geojson;

    /** 空间信息 */
    @ApiModelProperty(value = "空间信息", position = 7)
    @JsonIgnore
    @TableField(exist = false)
    private String geometry;

    /** BBOX */
    @ApiModelProperty(value = "BBOX", position = 8)
    private String bbox;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户 */
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 修改时间 */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 修改用户 */
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;

    /** 排序号 */
    @ApiModelProperty(value = "排序号", position = 13)
    private Long orderNum;
}
