
package vip.xiaonuo.biz.modular.org.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 机构列表选择器参数
 *
 * @author xuyuxiang
 * @date 2022/4/21 16:13
 **/
@Getter
@Setter
public class BizOrgSelectorOrgListParam {

    /** 父id */
    @ApiModelProperty(value = "父id")
    private String parentId;

    /** 名称关键词 */
    @ApiModelProperty(value = "名称关键词")
    private String searchKey;
}
