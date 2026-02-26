
package vip.xiaonuo.sys.modular.region.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 行政区划Id参数
 *
 * @author gago
 * @date  2025/08/26 15:08
 **/
@Getter
@Setter
public class RegionIdParam {

    /** 主键 */
    @ApiModelProperty(value = "主键", required = true)
    private Long id;

}
