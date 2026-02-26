
package vip.xiaonuo.mobile.modular.resource.param.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 移动端按钮查询参数
 *
 * @author xuyuxiang
 * @date 2022/7/27 18:40
 **/
@Getter
@Setter
public class MobileGlobalResourcePageParam {

    /** 当前页 */
    @ApiModelProperty(value = "当前页码")
    private Integer current;

    /** 每页条数 */
    @ApiModelProperty(value = "每页条数")
    private Integer size;

    /** 排序字段 */
    @ApiModelProperty(value = "排序字段，字段驼峰名称，如：userName")
    private String sortField;

    /** 排序方式 */
    @ApiModelProperty(value = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;

    /** 分类 */
    @ApiModelProperty(value = "分类")
    private String category;

    /** 标题 */
    @ApiModelProperty(value = "标题")
    private String title;
}
