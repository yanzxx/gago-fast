
package vip.xiaonuo.sys.modular.region.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 行政区划编辑参数
 *
 * @author gago
 * @date  2025/08/26 15:08
 **/
@Getter
@Setter
public class RegionEditParam {

    /** 主键 */
    @ApiModelProperty(value = "主键", required = true, position = 1)
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
    private String geojson;

    /** 空间信息 */
    @ApiModelProperty(value = "空间信息", position = 7)
    private String geometry;

    /** BBOX */
    @ApiModelProperty(value = "BBOX", position = 8)
    private String bbox;

    /** 排序号 */
    @ApiModelProperty(value = "排序号", position = 13)
    private Long orderNum;

}
