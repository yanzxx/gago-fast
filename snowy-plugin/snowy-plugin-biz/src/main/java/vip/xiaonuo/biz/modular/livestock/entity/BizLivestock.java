package vip.xiaonuo.biz.modular.livestock.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 牲畜登记实体
 */
@Getter
@Setter
@TableName("BIZ_LIVESTOCK")
public class BizLivestock extends CommonEntity implements TransPojo {

    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    @ApiModelProperty(value = "农场id", position = 3)
    private String farmId;

    @ApiModelProperty(value = "牲畜编号", position = 4)
    private String livestockNo;

    @ApiModelProperty(value = "畜种名称", position = 5)
    private String speciesName;

    @ApiModelProperty(value = "出生日期", position = 6)
    private String birthDate;

    @ApiModelProperty(value = "状态", position = 7)
    private String status;

    @ApiModelProperty(value = "防疫状态", position = 8)
    private String immunityStatus;

    @ApiModelProperty(value = "近期防疫注射时间", position = 9)
    private String lastImmunityDate;

    @ApiModelProperty(value = "项圈编号", position = 10)
    private String collarNo;

    @ApiModelProperty(value = "性别", position = 11)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String gender;

    @ApiModelProperty(value = "圈舍", position = 12)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String penNo;

    @ApiModelProperty(value = "登记时间", position = 13)
    private String registerDate;

    @ApiModelProperty(value = "备注", position = 14)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    @ApiModelProperty(value = "附件图片(JSON数组字符串)", position = 15)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String imageUrls;
}
