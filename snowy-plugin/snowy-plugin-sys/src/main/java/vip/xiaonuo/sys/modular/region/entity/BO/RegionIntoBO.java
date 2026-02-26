
package vip.xiaonuo.sys.modular.region.entity.BO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import vip.xiaonuo.common.annotation.ExcelConfig;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 行政区划 Excel导入实体
 *
 * @author gago
 * @date  2025/08/26 15:08
 **/
@Getter
@Setter
public class RegionIntoBO {

    /** 主键 */

    @ApiModelProperty(value = "主键", position = 1)
    @ExcelConfig(name = "主键")
    private Long id;

    /** 区划代码 */

    @ApiModelProperty(value = "区划代码", position = 2)
    @ExcelConfig(name = "区划代码")
    private String code;

    /** 区划名称 */

    @ApiModelProperty(value = "区划名称", position = 3)
    @ExcelConfig(name = "区划名称")
    private String name;

    /** 父级区划代码 */

    @ApiModelProperty(value = "父级区划代码", position = 4)
    @ExcelConfig(name = "父级区划代码")
    private String parentCode;

    /** 父级区划名称 */

    @ApiModelProperty(value = "父级区划名称", position = 5)
    @ExcelConfig(name = "父级区划名称")
    private String parentName;

    /** GEOJSON */

    @ApiModelProperty(value = "GEOJSON", position = 6)
    @ExcelConfig(name = "GEOJSON")
    private String geojson;

    /** 空间信息 */

    @ApiModelProperty(value = "空间信息", position = 7)
    @ExcelConfig(name = "空间信息")
    private String geometry;

    /** BBOX */

    @ApiModelProperty(value = "BBOX", position = 8)
    @ExcelConfig(name = "BBOX")
    private String bbox;

    /** 创建时间 */

    @ApiModelProperty(value = "创建时间", position = 9)
    private Date createTime;

    /** 创建用户 */

    @ApiModelProperty(value = "创建用户", position = 10)
    private String createUser;

    /** 修改时间 */

    @ApiModelProperty(value = "修改时间", position = 11)
    private Date updateTime;

    /** 修改用户 */

    @ApiModelProperty(value = "修改用户", position = 12)
    private String updateUser;

    /** 排序号 */

    @ApiModelProperty(value = "排序号", position = 13)
    @ExcelConfig(name = "排序号")
    private Long orderNum;
}
