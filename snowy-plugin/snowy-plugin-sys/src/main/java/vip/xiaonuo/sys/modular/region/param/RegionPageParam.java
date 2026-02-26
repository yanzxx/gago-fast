
package vip.xiaonuo.sys.modular.region.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.page.CommonPageEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 行政区划查询参数
 *
 * @author gago
 * @date  2025/08/26 15:08
 **/
@Getter
@Setter
public class RegionPageParam extends CommonPageEntity {

    /** 排序字段 */
    @ApiModelProperty(value = "排序字段，字段驼峰名称，如：userName")
    private String sortField;

    /** 排序方式 */
    @ApiModelProperty(value = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;

    /** 关键词 */
    @ApiModelProperty(value = "关键词")
    private String searchKey;

    /** 区划代码 */
    @ApiModelProperty(value = "区划代码")
    private String code;

    /** 区划名称 */
    @ApiModelProperty(value = "区划名称")
    private String name;

    /** 父级区划代码 */
    @ApiModelProperty(value = "父级区划代码")
    private String parentCode;

    /** 父级区划名称 */
    @ApiModelProperty(value = "父级区划名称")
    private String parentName;

}
