package vip.xiaonuo.biz.modular.insproduct.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 保险产品id参数
 */
@Getter
@Setter
public class BizInsProductIdParam {

    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "产品编码(兼容定位字段)", position = 2)
    private String productCode;
}
