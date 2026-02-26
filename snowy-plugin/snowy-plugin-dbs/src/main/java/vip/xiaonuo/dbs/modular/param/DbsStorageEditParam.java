
package vip.xiaonuo.dbs.modular.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 数据源编辑参数
 *
 * @author xuyuxiang
 * @date 2022/7/29 9:59
 */
@Getter
@Setter
public class DbsStorageEditParam {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 连接URL */
    @ApiModelProperty(value = "连接URL", required = true, position = 3)
    @NotBlank(message = "url不能为空")
    private String url;

    /** 用户名 */
    @ApiModelProperty(value = "用户名", required = true, position = 4)
    @NotBlank(message = "username不能为空")
    private String username;

    /** 密码 */
    @ApiModelProperty(value = "密码", required = true, position = 5)
    @NotBlank(message = "password不能为空")
    private String password;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 6)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 7)
    private String extJson;
}
