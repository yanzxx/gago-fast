package com.gago.bmp.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gago.bmp.pojo.AccessTokenResult;
import com.gago.bmp.pojo.WeatherAllInOneResult;
import com.gago.bmp.pojo.WeatherRealTimeResult;
import com.gago.bmp.service.BmpService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import vip.xiaonuo.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@Api(tags = "中台代理")
@ApiSupport(author = "GAGO", order = 5)
@RestController
@RequestMapping("/bmp")
@Validated
public class BmpController {

    @Resource
    private BmpService bmpService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("获取业务中台AccessToken")
    @GetMapping("/getAccessToken")
    public CommonResult<AccessTokenResult> getToken() {
        return CommonResult.data(bmpService.getAccessToken());
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation("获取Publisher中台文件数据")
    @GetMapping(value = "/data/publisher/{type}/{date}/{z}/{x}/{y}.{postfix}", produces = "application/octet-stream")
    public byte[] getPublisherDataFile(@PathVariable String type, @PathVariable String date, @PathVariable String z,
                                       @PathVariable String x, @PathVariable String y, @ApiParam("文件后缀名") @PathVariable String postfix) {
        return bmpService.getPublisherDataFile(type, date, z, x, y, postfix);
    }

    @ApiOperationSupport(order = 20)
    @ApiOperation("获取open-data数据")
    @GetMapping(value = "/data/openData/**", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getOpenData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/octet-stream;charset=UTF-8");
        IoUtil.write(response.getOutputStream(), true, bmpService.getOpenData(arguments));
    }

    @ApiOperationSupport(order = 30)
    @ApiOperation("获取open-data-json数据")
    @GetMapping(value = "/data/openDataJson/**")
    public CommonResult<JSON> openDataJson(HttpServletRequest request) {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        JSON json = JSONUtil.parse(bmpService.getOpenData(arguments));
        return CommonResult.data(json);
    }

    @ApiOperationSupport(order = 40)
    @ApiOperation("获取实时天气")
    @GetMapping("/weather/realTime")
    public CommonResult<WeatherRealTimeResult> getWeatherRealTime(@ApiParam("经度") @RequestParam BigDecimal lng,
                                                                  @ApiParam("纬度") @RequestParam BigDecimal lat) {
        return CommonResult.data(bmpService.getWeatherRealTime(lng, lat));
    }

    @ApiOperationSupport(order = 50)
    @ApiOperation("获取15日天气预报")
    @GetMapping("/weather/forecast/allInOne")
    public CommonResult<WeatherAllInOneResult> getWeatherForecastAllInOne(@ApiParam("经度") @RequestParam BigDecimal lng,
                                                                          @ApiParam("纬度") @RequestParam BigDecimal lat) {
        return CommonResult.data(bmpService.getWeatherForecastAllInOne(lng, lat));
    }

    @ApiOperationSupport(order = 60)
    @ApiOperation("获取气象预警")
    @GetMapping("/weather/alert")
    public CommonResult<JSONObject> getWeatherAlert() {
        return CommonResult.data(bmpService.getWeatherAlert());
    }
}
