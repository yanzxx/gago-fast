package com.gago.bmp.service;

import cn.hutool.json.JSONObject;
import com.gago.bmp.pojo.AccessTokenResult;
import com.gago.bmp.pojo.WeatherAllInOneResult;
import com.gago.bmp.pojo.WeatherRealTimeResult;

import java.math.BigDecimal;
import java.util.Date;

public interface BmpService {

    AccessTokenResult getAccessToken();

    byte[] getOpenData(String dataPath);

    WeatherRealTimeResult getWeatherRealTime(BigDecimal lng, BigDecimal lat);

    WeatherAllInOneResult getWeatherForecastAllInOne(BigDecimal lng, BigDecimal lat);

    byte[] getPublisherDataFile(String type, String date, String z, String x, String y, String postfix);

    JSONObject getWeatherAlert();

    BigDecimal getHistorySumTemperature(BigDecimal lng, BigDecimal lat, Date startDate, Date endDate);

    BigDecimal getHistoryApcp(BigDecimal lng, BigDecimal lat, Date startDate, Date endDate);
}
