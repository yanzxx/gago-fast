package vip.xiaonuo.biz.modular.supervision.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 农场统计参数
 */
@Getter
@Setter
public class BizSupervisionFarmStatsParam {

    @ApiModelProperty("养殖场id")
    private String farmId;
}
