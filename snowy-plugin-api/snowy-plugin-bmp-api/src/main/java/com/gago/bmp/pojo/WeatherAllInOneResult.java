package com.gago.bmp.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class WeatherAllInOneResult {

    @ApiModelProperty(value = "每分钟预报", position = 1)
    private List<MinutelyVo> minutely;

    @ApiModelProperty(value = "每小时预报", position = 2)
    private List<HourlyVo> hourly;

    @ApiModelProperty(value = "每日预报", position = 3)
    private List<DailyVo> daily;

    @Getter
    @Setter
    public static class MinutelyVo {

        private Date date;

        @ApiModelProperty(value = "降水量", position = 1)
        private BigDecimal precipitation;
    }

    @Getter
    @Setter
    public static class HourlyVo {

        private Date date;

        private BigDecimal tmp;

        private BigDecimal apcp;

        private BigDecimal rh;

        private BigDecimal srad;

        private BigDecimal soilm;

        private BigDecimal tsoil;

        private BigDecimal windSpeed;

        private BigDecimal windDirection;
    }

    @Getter
    @Setter
    public static class DailyVo {

        private Date date;

        @ApiModelProperty(value = "温度", position = 1)
        private BigDecimal tmp;

        @ApiModelProperty(value = "最高温", position = 2)
        private BigDecimal tmax;

        @ApiModelProperty(value = "最低温", position = 3)
        private BigDecimal tmin;

        @ApiModelProperty(value = "降水量", position = 4)
        private BigDecimal apcp;

        @ApiModelProperty(value = "相对湿度", position = 5)
        private BigDecimal rh;

        @ApiModelProperty(value = "辐射强度", position = 6)
        private BigDecimal srad;

        @ApiModelProperty(value = "土壤湿度", position = 7)
        private BigDecimal soilm;

        @ApiModelProperty(value = "土壤温度", position = 8)
        private BigDecimal tsoil;

        @ApiModelProperty(value = "天气描述", position = 9)
        private String weatherDescription;

        @ApiModelProperty(value = "风速", position = 10)
        private String windSpeed;

        @ApiModelProperty(value = "风向", position = 11)
        private String windDirection;
    }
}
