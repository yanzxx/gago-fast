
package vip.xiaonuo.gen.modular.basic.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 代码生成基础Id参数
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Getter
@Setter
public class GenBasicIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;

    @ApiModelProperty(value = "需要生产的文件名")
    private List<String> genFileNames;
}
