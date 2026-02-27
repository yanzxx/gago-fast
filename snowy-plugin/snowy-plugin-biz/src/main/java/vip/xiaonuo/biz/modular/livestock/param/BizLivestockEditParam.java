package vip.xiaonuo.biz.modular.livestock.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 牲畜登记编辑参数
 */
@Getter
@Setter
public class BizLivestockEditParam {

    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "牲畜编号", position = 2)
    private String livestockNo;

    @ApiModelProperty(value = "农场id", position = 3)
    private String farmId;

    @ApiModelProperty(value = "畜种名称", required = true, position = 4)
    @NotBlank(message = "speciesName不能为空")
    private String speciesName;

    @ApiModelProperty(value = "出生日期，格式yyyy-MM-dd", required = true, position = 5)
    @NotBlank(message = "birthDate不能为空")
    private String birthDate;

    @ApiModelProperty(value = "状态", required = true, position = 6)
    @NotBlank(message = "status不能为空")
    private String status;

    @ApiModelProperty(value = "防疫状态", required = true, position = 7)
    @NotBlank(message = "immunityStatus不能为空")
    private String immunityStatus;

    @ApiModelProperty(value = "近期防疫注射时间，格式yyyy-MM-dd", required = true, position = 8)
    @NotBlank(message = "lastImmunityDate不能为空")
    private String lastImmunityDate;

    @ApiModelProperty(value = "项圈编号", required = true, position = 9)
    @NotBlank(message = "collarNo不能为空")
    private String collarNo;

    @ApiModelProperty(value = "性别", position = 10)
    private String gender;

    @ApiModelProperty(value = "圈舍", position = 11)
    private String penNo;

    @ApiModelProperty(value = "备注", position = 12)
    private String remark;

    @ApiModelProperty(value = "附件图片(JSON数组字符串)", position = 13)
    private String imageUrls;
}
