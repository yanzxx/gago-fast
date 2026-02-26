
package vip.xiaonuo.biz.modular.tabular.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 岗位添加参数
 *
 * @author xuyuxiang
 * @date 2022/4/21 16:13
 **/
@Getter
@Setter
public class BizTabularDisplayAddParam {

    /** 机构id */
    @ApiModelProperty(value = "表单唯一ID，最好是当前表单的路由", required = true, position = 1)
    @NotBlank(message = "表单唯一ID")
    private String formId;

    /** 名称 */
    @ApiModelProperty(value = "展示的列名称", required = true, position = 2)
    @NotEmpty(message = "展示的列名称")
    private List<String> displayList;

}
