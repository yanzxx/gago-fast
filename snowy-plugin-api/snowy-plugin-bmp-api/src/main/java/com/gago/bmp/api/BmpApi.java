package com.gago.bmp.api;

import com.gago.bmp.pojo.AccessTokenResult;
import com.gago.bmp.pojo.WeatherAllInOneResult;

import java.math.BigDecimal;
import java.util.Date;

public interface BmpApi {

    AccessTokenResult getAccessToken();

    byte[] getOpenData(String dataPath);

    WeatherAllInOneResult getWeatherForecastAllInOne(BigDecimal lng, BigDecimal lat);

    BigDecimal getHistorySumTemperature(BigDecimal lng, BigDecimal lat, Date startDate, Date endDate);

    BigDecimal getHistoryApcp(BigDecimal lng, BigDecimal lat, Date startDate, Date endDate);
}
