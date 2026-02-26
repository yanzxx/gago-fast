
package vip.xiaonuo.dbs.modular.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 数据源Id参数
 *
 * @author xuyuxiang
 * @date 2022/7/29 9:59
 */
@Getter
@Setter
public class DbsStorageIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
