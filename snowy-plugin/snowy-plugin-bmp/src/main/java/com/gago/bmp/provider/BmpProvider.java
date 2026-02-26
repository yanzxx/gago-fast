package com.gago.bmp.provider;

import com.gago.bmp.api.BmpApi;
import com.gago.bmp.pojo.AccessTokenResult;
import com.gago.bmp.pojo.WeatherAllInOneResult;
import com.gago.bmp.service.BmpService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class BmpProvider implements BmpApi {

    @Resource
    private BmpService bmpService;

    @Override
    public AccessTokenResult getAccessToken() {
        return bmpService.getAccessToken();
    }

    @Override
    public byte[] getOpenData(String dataPath) {
        return bmpService.getOpenData(dataPath);
    }

    @Override
    public WeatherAllInOneResult getWeatherForecastAllInOne(BigDecimal lng, BigDecimal lat) {
        return bmpService.getWeatherForecastAllInOne(lng, lat);
    }

    @Override
    public BigDecimal getHistorySumTemperature(BigDecimal lng, BigDecimal lat, Date startDate, Date endDate) {
        return bmpService.getHistorySumTemperature(lng, lat, startDate, endDate);
    }

    @Override
    public BigDecimal getHistoryApcp(BigDecimal lng, BigDecimal lat, Date startDate, Date endDate) {
        return bmpService.getHistoryApcp(lng, lat, startDate, endDate);
    }
}
