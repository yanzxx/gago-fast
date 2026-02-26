package com.gago.bmp.core.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gago.bmp.core.retrofit.AccessTokenInterceptor;
import com.gago.bmp.core.retrofit.BmpHttpApi;
import com.gago.bmp.service.BmpService;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.retrofit.AggregativeConverterFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(BmpProps.class)
@Configuration
@Slf4j
public class BmpConfig {

    @Resource
    private BmpProps props;

    @PostConstruct
    public void init() {
        if (StrUtil.hasBlank(props.getBaseUrl(), props.getAccessKeyId(), props.getAccessKeySecret(),
                props.getProjectCode())) {
            log.warn("请检查中台相关配置是否已填写：\n{}", JSONUtil.toJsonPrettyStr(props));
        }
    }

    @Bean
    public BmpHttpApi bmpHttpApi(BmpService bmpService) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .callTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new AccessTokenInterceptor(bmpService))
                .build();
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(StrUtil.endWith(props.getBaseUrl(), '/') ? props.getBaseUrl() : props.getBaseUrl() + "/")
                .addConverterFactory(AggregativeConverterFactory.create(JacksonConverterFactory.create(new ObjectMapper())))
                .build().create(BmpHttpApi.class);
    }

    @Bean
    public Docket bmpDocApi(OpenApiExtensionResolver openApiExtensionResolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Z业务中台")
                        .description("Z业务中台")
                        .termsOfServiceUrl("https://gagogroup.cn")
                        .contact(new Contact("GAGO", "https://gagogroup.cn", "gago@gagogroup.com"))
                        .version("1.0.0")
                        .build())
                .globalResponseMessage(RequestMethod.GET, CommonResult.responseList())
                .globalResponseMessage(RequestMethod.POST, CommonResult.responseList())
                .groupName("Z业务中台")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.gago.bmp"))
                .paths(PathSelectors.any())
                .build().extensions(openApiExtensionResolver.buildExtensions("Z业务中台"));
    }
}
