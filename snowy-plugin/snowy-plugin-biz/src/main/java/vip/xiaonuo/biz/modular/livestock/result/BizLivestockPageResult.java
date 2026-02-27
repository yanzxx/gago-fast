package vip.xiaonuo.biz.modular.livestock.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 牲畜登记列表项
 */
@Getter
@Setter
public class BizLivestockPageResult {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("牲畜主键ID（兼容字段）")
    private String livestockId;

    @ApiModelProperty("牲畜编号")
    private String livestockNo;

    @ApiModelProperty("农场id")
    private String farmId;

    @ApiModelProperty("畜种名称")
    private String speciesName;

    @ApiModelProperty("出生日期")
    private String birthDate;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("防疫状态")
    private String immunityStatus;

    @ApiModelProperty("近期防疫注射时间")
    private String lastImmunityDate;

    @ApiModelProperty("项圈编号")
    private String collarNo;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("圈舍")
    private String penNo;

    @ApiModelProperty("登记时间")
    private String registerDate;
}
