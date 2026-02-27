package vip.xiaonuo.biz.modular.finproduct.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 金融产品实体
 */
@Getter
@Setter
@TableName("BIZ_FIN_PRODUCT")
public class BizFinProduct extends CommonEntity implements TransPojo {

    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    @ApiModelProperty(value = "农场id", position = 3)
    private String farmId;

    @ApiModelProperty(value = "产品名称", position = 4)
    private String productName;

    @ApiModelProperty(value = "产品编码", position = 5)
    private String productCode;

    @ApiModelProperty(value = "适用畜种编码，逗号分隔", position = 6)
    private String speciesCodes;

    @ApiModelProperty(value = "最低额度", position = 7)
    private java.math.BigDecimal amountMin;

    @ApiModelProperty(value = "最高额度", position = 8)
    private java.math.BigDecimal amountMax;

    @ApiModelProperty(value = "年化利率", position = 9)
    private java.math.BigDecimal annualRate;

    @ApiModelProperty(value = "期限(月)", position = 10)
    private Integer termMonths;

    @ApiModelProperty(value = "还款方式", position = 11)
    private String repayType;

    @ApiModelProperty(value = "状态", position = 12)
    private String status;

    @ApiModelProperty(value = "产品说明", position = 13)
    private String remark;
}
