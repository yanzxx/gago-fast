package vip.xiaonuo.biz.modular.insproduct.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 保险产品分页参数
 */
@Getter
@Setter
public class BizInsProductPageParam {

    @ApiModelProperty(value = "当前页码")
    private Integer current;

    @ApiModelProperty(value = "每页条数")
    private Integer size;

    @ApiModelProperty(value = "养殖场id")
    private String farmId;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "状态")
    private String status;
}
