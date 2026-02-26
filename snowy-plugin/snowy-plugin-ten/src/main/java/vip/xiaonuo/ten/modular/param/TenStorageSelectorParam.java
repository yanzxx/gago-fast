
package vip.xiaonuo.ten.modular.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 多租户查询参数
 *
 * @author xuyuxiang
 * @date 2022/7/29 11:20
 */
@Getter
@Setter
public class TenStorageSelectorParam {
    /** 排序字段 */
    @ApiModelProperty(value = "排序字段，字段驼峰名称，如：userName")
    private String sortField;

    /** 排序方式 */
    @ApiModelProperty(value = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;

    /** 隔离分类 */
    @ApiModelProperty(value = "隔离分类")
    private String category;

    /** 名称关键词 */
    @ApiModelProperty(value = "名称关键词")
    private String searchKey;
}
