package vip.xiaonuo.biz.modular.livestock.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 牲畜登记查询参数
 */
@Getter
@Setter
public class BizLivestockIdParam {

    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "牲畜编号", position = 2)
    private String livestockNo;
}
