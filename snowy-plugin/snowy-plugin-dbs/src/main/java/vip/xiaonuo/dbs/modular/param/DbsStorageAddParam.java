
package vip.xiaonuo.dbs.modular.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 数据源添加参数
 *
 * @author xuyuxiang
 * @date 2022/7/29 9:59
 */
@Getter
@Setter
public class DbsStorageAddParam {

    /** 名称 */
    @ApiModelProperty(value = "名称", required = true, position = 1)
    @NotBlank(message = "poolName不能为空")
    private String poolName;

    /** 连接URL */
    @ApiModelProperty(value = "连接URL", required = true, position = 2)
    @NotBlank(message = "url不能为空")
    private String url;

    /** 用户名 */
    @ApiModelProperty(value = "用户名", required = true, position = 3)
    @NotBlank(message = "username不能为空")
    private String username;

    /** 密码 */
    @ApiModelProperty(value = "密码", required = true, position = 4)
    @NotBlank(message = "password不能为空")
    private String password;

    /** 驱动名称 */
    @ApiModelProperty(value = "驱动名称", required = true, position = 5)
    @NotBlank(message = "driverName不能为空")
    private String driverName;

    /** 分类 */
    @ApiModelProperty(value = "分类", required = true, position = 6)
    @NotBlank(message = "category不能为空")
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 7)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 8)
    private String extJson;
}
