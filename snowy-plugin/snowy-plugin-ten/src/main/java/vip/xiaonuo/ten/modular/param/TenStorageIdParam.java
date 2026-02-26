
package vip.xiaonuo.ten.modular.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 多租户Id参数
 *
 * @author xuyuxiang
 * @date 2022/7/29 11:19
 */
@Getter
@Setter
public class TenStorageIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
