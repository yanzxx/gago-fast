
package vip.xiaonuo.ten.modular.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 多租户模块所需要用到的数据源选择器的结果
 *
 * @author xuyuxiang
 * @date 2022/7/19 18:55
 **/
@Getter
@Setter
public class TenDbsSelectorResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 2)
    private String poolName;
}
