package vip.xiaonuo.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerMapping;

import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.SpringfoxWebConfiguration;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;
import springfox.documentation.spring.web.WebMvcPropertySourcedRequestMappingHandlerMapping;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger.configuration.SwaggerCommonConfiguration;
import springfox.documentation.swagger2.configuration.Swagger2JacksonModule;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;

/**
 * Swagger配置类
 * 手动初始化Swagger组件，替代@EnableSwagger2WebMvc注解
 *
 * @author gago-admin
 * @date 2023/8/21
 */
@Configuration
@ConditionalOnExpression("${knife4j.enable}==true and ${knife4j.production}==false")
@Import({
        SpringfoxWebConfiguration.class, // 提供核心Bean（如DocumentationCache）
        SpringfoxWebMvcConfiguration.class, // 提供MVC相关配置
        SwaggerCommonConfiguration.class // 提供Swagger通用组件
})
@ComponentScan(basePackages = {
        "springfox.documentation.swagger2.readers.parameter",
        "springfox.documentation.swagger2.mappers"
})
public class SwaggerConfiguration {

    /**
     * 提供Swagger2的JSON序列化支持
     */
    @Bean
    public JacksonModuleRegistrar swagger2Module() {
        return new Swagger2JacksonModule();
    }

    /**
     * 处理Swagger API文档请求的映射
     */
    @Bean
    public HandlerMapping swagger2ControllerMapping(
            Environment environment,
            DocumentationCache documentationCache,
            ServiceModelToSwagger2Mapper mapper,
            JsonSerializer jsonSerializer) {

        return new WebMvcPropertySourcedRequestMappingHandlerMapping(
                environment,
                new Swagger2ControllerWebMvc(environment, documentationCache, mapper, jsonSerializer));
    }
}