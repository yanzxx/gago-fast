package vip.xiaonuo.biz.modular.livestock.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 牲畜登记分页参数
 */
@Getter
@Setter
public class BizLivestockPageParam {

    @ApiModelProperty(value = "当前页码")
    private Integer current;

    @ApiModelProperty(value = "每页条数")
    private Integer size;

    @ApiModelProperty(value = "农场id")
    private String farmId;

    @ApiModelProperty(value = "畜种名称")
    private String speciesName;

    @ApiModelProperty(value = "项圈编号")
    private String collarNo;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "防疫状态")
    private String immunityStatus;

    @ApiModelProperty(value = "登记起始日期，格式yyyy-MM-dd")
    private String registerStartDate;

    @ApiModelProperty(value = "登记结束日期，格式yyyy-MM-dd")
    private String registerEndDate;
}
