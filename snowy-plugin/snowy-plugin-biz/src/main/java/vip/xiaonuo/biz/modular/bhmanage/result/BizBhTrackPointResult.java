package vip.xiaonuo.biz.modular.bhmanage.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 保后轨迹点
 */
@Getter
@Setter
public class BizBhTrackPointResult {

    @ApiModelProperty("轨迹点id")
    private String id;

    @ApiModelProperty("经度")
    private Double lng;

    @ApiModelProperty("纬度")
    private Double lat;

    @ApiModelProperty("事件时间")
    private String eventTime;

    @ApiModelProperty("事件描述")
    private String eventDesc;
}
