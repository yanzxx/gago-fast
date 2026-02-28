package vip.xiaonuo.biz.modular.supervision.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 地图养殖场点位
 */
@Getter
@Setter
public class BizSupervisionMapPointResult {

    @ApiModelProperty("养殖场id")
    private String farmId;

    @ApiModelProperty("养殖场名称")
    private String farmName;

    @ApiModelProperty("经度")
    private Double longitude;

    @ApiModelProperty("纬度")
    private Double latitude;

    @ApiModelProperty("存栏数量")
    private Long inStockCount;

    @ApiModelProperty("健康指数")
    private Integer healthScore;

    @ApiModelProperty("风险等级")
    private String riskLevel;
}
