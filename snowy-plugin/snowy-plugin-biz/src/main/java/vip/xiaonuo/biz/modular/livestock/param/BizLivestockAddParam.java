package vip.xiaonuo.biz.modular.livestock.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 牲畜登记新增参数
 */
@Getter
@Setter
public class BizLivestockAddParam {

    @ApiModelProperty(value = "农场id")
    private String farmId;

    @ApiModelProperty(value = "畜种名称", required = true, position = 1)
    @NotBlank(message = "speciesName不能为空")
    private String speciesName;

    @ApiModelProperty(value = "出生日期，格式yyyy-MM-dd", required = true, position = 2)
    @NotBlank(message = "birthDate不能为空")
    private String birthDate;

    @ApiModelProperty(value = "防疫状态", required = true, position = 3)
    @NotBlank(message = "immunityStatus不能为空")
    private String immunityStatus;

    @ApiModelProperty(value = "近期防疫注射时间，格式yyyy-MM-dd", required = true, position = 4)
    @NotBlank(message = "lastImmunityDate不能为空")
    private String lastImmunityDate;

    @ApiModelProperty(value = "项圈编号", required = true, position = 5)
    @NotBlank(message = "collarNo不能为空")
    private String collarNo;

    @ApiModelProperty(value = "性别", position = 6)
    private String gender;

    @ApiModelProperty(value = "圈舍", position = 7)
    private String penNo;

    @ApiModelProperty(value = "备注", position = 8)
    private String remark;

    @ApiModelProperty(value = "附件图片(JSON数组字符串)", position = 9)
    private String imageUrls;

    @ApiModelProperty(value = "状态（不传时默认IN_STOCK）", position = 10)
    private String status;
}
