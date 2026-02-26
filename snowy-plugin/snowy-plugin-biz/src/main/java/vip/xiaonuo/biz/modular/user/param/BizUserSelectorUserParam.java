
package vip.xiaonuo.biz.modular.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户选择器参数
 *
 * @author xuyuxiang
 * @date 2022/7/26 16:03
 **/
@Getter
@Setter
public class BizUserSelectorUserParam {

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private String orgId;

    /** 姓名关键词 */
    @ApiModelProperty(value = "姓名关键词")
    private String searchKey;
}
