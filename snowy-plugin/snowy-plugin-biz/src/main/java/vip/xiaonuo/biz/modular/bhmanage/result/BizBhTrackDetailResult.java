package vip.xiaonuo.biz.modular.bhmanage.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 保后轨迹详情
 */
@Getter
@Setter
public class BizBhTrackDetailResult {

    @ApiModelProperty("异常id")
    private String anomalyId;

    @ApiModelProperty("轨迹点")
    private List<BizBhTrackPointResult> points;
}
