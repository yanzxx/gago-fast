package vip.xiaonuo.biz.modular.livestock.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 畜种下拉选项
 */
@Getter
@Setter
public class BizLivestockSpeciesOptionResult {

    @ApiModelProperty("显示名称")
    private String label;

    @ApiModelProperty("提交值")
    private String value;

    @ApiModelProperty("编码")
    private String code;
}
