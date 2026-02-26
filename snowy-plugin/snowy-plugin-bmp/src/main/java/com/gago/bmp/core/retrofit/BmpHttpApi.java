package com.gago.bmp.core.retrofit;

import cn.hutool.json.JSONObject;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.math.BigDecimal;

public interface BmpHttpApi {

    @POST("/api/v1/gago/auth/token")
    Call<JSONObject> getAccessToken(@Body RequestBody body);

    @GET("/api/v2/publisher/data/{type}/{date}/{z}/{x}/{y}/{postfix}")
    Call<byte[]> getPublisherDataFile(@Path("type") String type, @Path("date") String date, @Path("z") String z, @Path("x") String x, @Path("y") String y, @Path("postfix") String postfix);

    /**
     * 获取实时天气
     */
    @GET("/api/v2/weather/actual/realtime")
    Call<JSONObject> getWeatherRealTime(@Query("lon") BigDecimal lng, @Query("lat") BigDecimal lat);

    /**
     * 获取15日天气预报
     */
    @GET("/api/v4.1/weather/coordinate/forecast_all_in_one")
    Call<JSONObject> getWeatherForecastAllInOne(@Query("lon") BigDecimal lng, @Query("lat") BigDecimal lat);

    /**
     * 获取阿里云 OSS 开放数据
     * <a href="https://gago.yuque.com/nxwayb/mur43a/ks6fie">接口文档</a>
     */
    @GET("/api/v1/open-data/{dataPath}")
    Call<byte[]> getOpenData(@Path(value = "dataPath", encoded = true) String dataPath);

    /**
     * 获取气象预警
     */
    @GET("/api/v4/alert")
    Call<JSONObject> getWeatherAlert(@Query("n") String region);

    /**
     * 获取历史积温
     */
    @GET("/api/v4/weather/analysis/history_sum_temperature")
    Call<JSONObject> getHistorySumTemperature(@Query("lon") BigDecimal lng, @Query("lat") BigDecimal lat, @Query("from_date") String startDate, @Query("to_date") String endDate);

    /**
     * 获取历史积雨
     */
    @GET("/api/v4/weather/analysis/history_apcp")
    Call<JSONObject> getHistoryApcp(@Query("lon") BigDecimal lng, @Query("lat") BigDecimal lat, @Query("from_date") String startDate, @Query("to_date") String endDate);

    /**
     * 获取区域长势时间
     * <a href="https://alidocs.dingtalk.com/i/nodes/dQPGYqjpJYeGOorMCaZLqK3MJakx1Z5N">接口文档</a>
     */
    @GET("/v1/cropgrowth/fsn10m/region/time")
    Call<JSONObject> getRegionFusionGrowthTimeline(@Header("projectId") String projectId, @Query("sId") String sid, @Query("maskTag") String maskTag);

    /**
     * 获取区域长势均值
     * <a href="https://alidocs.dingtalk.com/i/nodes/dQPGYqjpJYeGOorMCaZLqK3MJakx1Z5N">接口文档</a>
     */
    @GET("/v1/cropgrowth/fsn10m/region/mean-stat")
    Call<JSONObject> getRegionFusionGrowthMeanStat(@Header("projectId") String projectId, @Query("sId") String sid,
                                                   @Query("regionId") String regionId, @Query("maskTag") String maskTag,
                                                   @Query("date") String date);

    /**
     * 获取区域长势阈值分级
     * <a href="https://alidocs.dingtalk.com/i/nodes/dQPGYqjpJYeGOorMCaZLqK3MJakx1Z5N">接口文档</a>
     */
    @GET("/v1/cropgrowth/fsn10m/region/class-threshold-stat")
    Call<JSONObject> getRegionFusionGrowthClassThresholdStat(@Header("projectId") String projectId, @Query("sId") String sid,
                                                             @Query("regionId") String regionId, @Query("maskTag") String maskTag,
                                                             @Query("date") String date);

    /**
     * 获取区域苗情统计
     * <a href="https://alidocs.dingtalk.com/i/nodes/dQPGYqjpJYeGOorMCaZLqK3MJakx1Z5N">接口文档</a>
     */
    @GET("/v1/cropgrowth/fsn10m/region/seeding-self-stat")
    Call<JSONObject> getRegionFusionSeedingSelfStat(@Header("projectId") String projectId, @Query("sId") String sid,
                                                    @Query("regionId") String regionId, @Query("maskTag") String maskTag,
                                                    @Query("date") String date);
}
