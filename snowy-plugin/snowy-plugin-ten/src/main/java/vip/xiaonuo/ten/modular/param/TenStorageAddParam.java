
package vip.xiaonuo.ten.modular.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 多租户添加参数
 *
 * @author xuyuxiang
 * @date 2022/7/29 11:19
 */
@Getter
@Setter
public class TenStorageAddParam {

    /** 名称 */
    @ApiModelProperty(value = "名称", required = true, position = 1)
    @NotBlank(message = "name不能为空")
    private String name;

    /** 绑定域名 */
    @ApiModelProperty(value = "绑定域名", required = true, position = 2)
    @NotBlank(message = "domain不能为空")
    private String domain;

    /** 分类 */
    @ApiModelProperty(value = "分类", required = true, position = 3)
    @NotBlank(message = "category不能为空")
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 4)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 数据源id */
    @ApiModelProperty(value = "数据源id", position = 5)
    private String dbsId;

    /** 数据源名称 */
    @ApiModelProperty(value = "数据源名称", position = 6)
    private String dbsName;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 7)
    private String extJson;
}
