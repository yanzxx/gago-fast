package com.gago.bmp.pojo;

import cn.hutool.core.annotation.Alias;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class WeatherRealTimeResult {

    @ApiModelProperty(value = "温度，单位℃", position = 1)
    private BigDecimal temperature;

    @ApiModelProperty(value = "天气情况，枚举范围：晴天、晴夜、多云、阴、雨、雪、风、雾霾沙尘", position = 2)
    private String weatherDescription;

    @ApiModelProperty(value = "湿度，单位％", position = 3)
    private BigDecimal humidity;

    @ApiModelProperty(value = "风级", position = 4)
    private Integer windPower;

    @ApiModelProperty(value = "降雨量", position = 5)
    private BigDecimal precipitation;

    @ApiModelProperty(value = "风向", position = 6)
    private String windDirection;

    @ApiModelProperty(value = "时间", position = 7)
    private Date time;

    @ApiModelProperty(value = "地表10米风速", position = 8)
    @Alias("windspeed")
    private BigDecimal windSpeed;
}
