package vip.xiaonuo.biz.modular.loan.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 贷款申请id参数
 */
@Getter
@Setter
public class BizLoanApplyIdParam {

    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "申请单号(兼容定位字段)", position = 2)
    private String applyNo;
}
