package vip.xiaonuo.biz.modular.insapply.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 投保记录id参数
 */
@Getter
@Setter
public class BizInsApplyIdParam {

    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "投保单号(兼容定位字段)", position = 2)
    private String applyNo;
}
