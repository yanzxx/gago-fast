package com.gago.bmp.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gago.bmp.core.config.BmpProps;
import com.gago.bmp.core.retrofit.BmpHttpApi;
import com.gago.bmp.pojo.AccessTokenResult;
import com.gago.bmp.pojo.WeatherAllInOneResult;
import com.gago.bmp.pojo.WeatherRealTimeResult;
import com.gago.bmp.service.BmpService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class BmpServiceImpl implements BmpService {

    private static final String ACCESS_TOKEN_CACHE_KEY = "bmp-access-token";

    private static final String WEATHER_ALL_IN_ONE_CACHE_KEY = "bmp-weather-all-in-one-";

    @Resource
    private BmpProps bmpProps;

    @Resource
    private BmpHttpApi bmpHttpApi;

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Override
    public AccessTokenResult getAccessToken() {
        AccessTokenResult accessTokenResult = new AccessTokenResult();

        // 先检查缓存中是否保存了AccessToken
        Object existingAccessToken = commonCacheOperator.get(ACCESS_TOKEN_CACHE_KEY);
        if (ObjectUtil.isNotEmpty(existingAccessToken)) {
            accessTokenResult.setAccessToken(Convert.toStr(existingAccessToken));
            accessTokenResult.setSuccess(true);
        } else {
            String sign = DigestUtil.md5Hex(bmpProps.getAccessKeySecret() + bmpProps.getAccessKeyId()
                    + bmpProps.getAccessKeySecret());
            JSONObject bodyJson = JSONUtil.createObj()
                    .set("accessKeyId", bmpProps.getAccessKeyId())
                    .set("sign", sign);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyJson.toString());

            try {
                JSONObject responseBody = bmpHttpApi.getAccessToken(requestBody).execute().body();
                if ("200".equals(responseBody.getStr("code"))) {
                    String accessToken = responseBody.getJSONObject("data").getStr("accessToken");
                    Integer expires = responseBody.getJSONObject("data").getInt("expires");
                    accessTokenResult.setAccessToken(accessToken);
                    accessTokenResult.setSuccess(true);
                    commonCacheOperator.put(ACCESS_TOKEN_CACHE_KEY, accessToken, expires);
                } else {
                    accessTokenResult.setSuccess(false);
                    accessTokenResult.setMsg(responseBody.getStr("msg"));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return accessTokenResult;
    }

    @Override
    public byte[] getOpenData(String dataPath) {
        try {
            Response<byte[]> response = bmpHttpApi.getOpenData(dataPath).execute();
            return response.body();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public WeatherRealTimeResult getWeatherRealTime(BigDecimal lng, BigDecimal lat) {
        try {
            Response<JSONObject> response = bmpHttpApi.getWeatherRealTime(lng, lat).execute();
            JSONObject resObj = response.body();
            return resObj.getJSONObject("data").toBean(WeatherRealTimeResult.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public WeatherAllInOneResult getWeatherForecastAllInOne(BigDecimal lng, BigDecimal lat) {
        try {
            String key = WEATHER_ALL_IN_ONE_CACHE_KEY + lng.toPlainString() + lat.toPlainString();
            Object resObj = commonCacheOperator.get(key);
            if (ObjectUtil.isEmpty(resObj)) {
                Response<JSONObject> response = bmpHttpApi.getWeatherForecastAllInOne(lng, lat).execute();
                if (response.body() != null) {
                    resObj = response.body();
                    if (JSONUtil.parseObj(resObj).getJSONObject("data") != null) {
                        commonCacheOperator.put(key, resObj, 60 * 60);
                    }
                }
            }
            JSONObject obj = JSONUtil.parseObj(resObj);
            return obj.getJSONObject("data").getJSONObject("forecastWeather").toBean(WeatherAllInOneResult.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public byte[] getPublisherDataFile(String type, String date, String z, String x, String y, String postfix) {
        try {
            Response<byte[]> res = bmpHttpApi.getPublisherDataFile(type, date, z, x, y, postfix).execute();
            return res.body();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public JSONObject getWeatherAlert() {
        try {
            Response<JSONObject> response = bmpHttpApi.getWeatherAlert(bmpProps.getRegionName()).execute();
            return response.body();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public BigDecimal getHistorySumTemperature(BigDecimal lng, BigDecimal lat, Date startDate, Date endDate) {
        try {
            Response<JSONObject> response = bmpHttpApi.getHistorySumTemperature(lng, lat, DateUtil.formatDate(startDate),
                    DateUtil.formatDate(endDate)).execute();
            return response.body().getJSONObject("data").getBigDecimal("sumTemperature");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public BigDecimal getHistoryApcp(BigDecimal lng, BigDecimal lat, Date startDate, Date endDate) {
        try {
            Response<JSONObject> response = bmpHttpApi.getHistoryApcp(lng, lat, DateUtil.formatDate(startDate),
                    DateUtil.formatDate(endDate)).execute();
            return response.body().getJSONObject("data").getBigDecimal("apcp");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(e.getMessage());
        }
    }
}
