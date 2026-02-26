
package vip.xiaonuo.gen.modular.basic.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 代码生成基础查询参数
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 */
@Getter
@Setter
public class GenBasicPageParam {

    /** 当前页 */
    @ApiModelProperty(value = "当前页码")
    private Integer current;

    /** 每页条数 */
    @ApiModelProperty(value = "每页条数")
    private Integer size;

    @ApiModelProperty(value = "业务名")
    private String busName;

    @ApiModelProperty(value = "功能名")
    private String functionName;

    @ApiModelProperty(value = "类名")
    private String className;

    /** 排序字段 */
    @ApiModelProperty(value = "排序字段，字段驼峰名称，如：userName")
    private String sortField;

    /** 排序方式 */
    @ApiModelProperty(value = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;
}
