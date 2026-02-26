
package vip.xiaonuo.gen.modular.basic.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 数据源库表参数
 *
 * @author xuyuxiang
 * @date 2022/7/29 9:59
 */
@Getter
@Setter
public class GenBasicDbsTableParam {

    /** 数据源Id */
    @ApiModelProperty(value = "数据源Id", required = true, position = 1)
    @NotBlank(message = "dbsId不能为空")
    private String dbsId;
}
