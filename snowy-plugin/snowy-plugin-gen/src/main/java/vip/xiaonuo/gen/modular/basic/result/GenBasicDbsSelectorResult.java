
package vip.xiaonuo.gen.modular.basic.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 代码生成模块所需要用到的数据源选择器的结果
 *
 * @author xuyuxiang
 * @date 2022/7/19 18:55
 **/
@Getter
@Setter
public class GenBasicDbsSelectorResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 2)
    private String poolName;
}
