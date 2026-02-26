
package vip.xiaonuo.ten.modular.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 多租户编辑参数
 *
 * @author xuyuxiang
 * @date 2022/7/29 11:19
 */
@Getter
@Setter
public class TenStorageEditParam {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 名称 */
    @ApiModelProperty(value = "名称", required = true, position = 2)
    @NotBlank(message = "name不能为空")
    private String name;

    /** 绑定域名 */
    @ApiModelProperty(value = "绑定域名", required = true, position = 3)
    @NotBlank(message = "domain不能为空")
    private String domain;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 4)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 5)
    private String extJson;
}
