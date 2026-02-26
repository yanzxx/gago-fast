
package vip.xiaonuo.pay.core.config;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.pay.core.wx.WxPayApi;
import vip.xiaonuo.pay.core.wx.WxPayApiConfigKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付相关配置
 *
 * @author xuyuxiang
 * @date 2022/7/7 16:18
 **/
@Configuration
public class PayConfigure implements WebMvcConfigurer{

    /** 支付宝支付相关配置，公钥证书模式 */
    // 应用编号
    private static final String SNOWY_PAY_ALI_APP_ID_KEY = "SNOWY_PAY_ALI_APP_ID";
    // 应用私钥
    private static final String SNOWY_PAY_ALI_PRIVATE_KEY_KEY = "SNOWY_PAY_ALI_PRIVATE_KEY";
    // 商家用户id
    private static final String SNOWY_API_ALI_MCH_USER_ID_KEY = "SNOWY_PAY_ALI_MCH_USER_ID";
    // 支付宝公钥证书
    private static final String SNOWY_PAY_ALI_CERT_PATH_KEY = "SNOWY_PAY_ALI_CERT_PATH";
    // 支付宝根证书路径
    private static final String SNOWY_PAY_ALI_ROOT_CERT_PATH_KEY = "SNOWY_PAY_ALI_ROOT_CERT_PATH";
    // 应用公钥证书路径
    private static final String SNOWY_PAY_ALI_APP_CERT_PATH_KEY = "SNOWY_PAY_ALI_APP_CERT_PATH";
    // 支付宝支付网关
    private static final String SNOWY_PAY_ALI_SERVER_URL_KEY = "SNOWY_PAY_ALI_SERVER_URL";
    // 支付回调地址
    private static final String SNOWY_PAY_ALI_NOTIFY_URL_KEY = "SNOWY_PAY_ALI_NOTIFY_URL";

    /** 微信相关配置 */
    // 应用编号
    private static final String SNOWY_PAY_WX_APP_ID_KEY = "SNOWY_PAY_WX_APP_ID";
    // 公众号AppSecret
    private static final String SNOWY_PAY_WX_APP_SECRET_KEY = "SNOWY_PAY_WX_APP_SECRET";
    // 商户号
    private static final String SNOWY_PAY_WX_MCH_ID_KEY = "SNOWY_PAY_WX_MCH_ID";
    // 商户V2密钥
    private static final String SNOWY_PAY_WX_MCH_KEY_KEY = "SNOWY_PAY_WX_MCH_KEY";
    // p12证书
    private static final String SNOWY_PAY_WX_KEY_PATH_KEY = "SNOWY_PAY_WX_KEY_PATH";
    // apiclient_key.pem证书
    private static final String SNOWY_PAY_WX_PRIVATE_KEY_PATH_KEY = "SNOWY_PAY_WX_PRIVATE_KEY_PATH";
    // apiclient_cert.pem证书
    private static final String SNOWY_PAY_WX_PRIVATE_CERT_PATH_KEY = "SNOWY_PAY_WX_PRIVATE_CERT_PATH";
    // API V3证书序列号值
    private static final String SNOWY_PAY_WX_CERT_SERIAL_NO_KEY = "SNOWY_PAY_WX_CERT_SERIAL_NO";
    // API V3秘钥值
    private static final String SNOWY_PAY_WX_API_V3_KEY_KEY = "SNOWY_PAY_WX_API_V3_KEY";
    // 支付回调地址
    private static final String SNOWY_PAY_WX_NOTIFY_URL_KEY = "SNOWY_PAY_WX_NOTIFY_URL";
    // 退款回调地址
    private static final String SNOWY_PAY_WX_REFUND_NOTIFY_URL_KEY = "SNOWY_PAY_WX_REFUND_NOTIFY_URL";


    /**
     * 添加支付拦截器
     *
     * @author xuyuxiang
     * @date 2020/8/11 17:03
     **/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest httpServletRequest,
                                     @NonNull HttpServletResponse httpServletResponse,
                                     @NonNull Object handler) {
                if (HandlerMethod.class.equals(handler.getClass())) {
                    // 缓存支付宝支付配置
                    AliPayApiConfig aliPayApiConfig = getAliPayApiConfig();
                    AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
                    // 缓存微信支付配置
                    WxPayConfig wxPayApiConfig = getWxPayApiConfig();
                    WxPayApiConfigKit.setThreadLocalWxPayConfig(wxPayApiConfig);
                    return true;
                }
                return false;
            }
        }).addPathPatterns("/pay/**");
    }

    /**
     * 获取支付宝支付配置
     *
     * @author xuyuxiang
     * @date 2022/8/16 14:08
     **/
    public static AliPayApiConfig getAliPayApiConfig() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String appId = devConfigApi.getValueByKey(SNOWY_PAY_ALI_APP_ID_KEY);
        if(ObjectUtil.isEmpty(appId)) {
            throw new CommonException("支付宝支付参数未正确配置：appId为空");
        }
        String privateKey = devConfigApi.getValueByKey(SNOWY_PAY_ALI_PRIVATE_KEY_KEY);
        if(ObjectUtil.isEmpty(privateKey)) {
            throw new CommonException("支付宝支付参数未正确配置：privateKey为空");
        }
        String mchUserId = devConfigApi.getValueByKey(SNOWY_API_ALI_MCH_USER_ID_KEY);
        if(ObjectUtil.isEmpty(mchUserId)) {
            throw new CommonException("支付宝支付参数未正确配置：mchUserId为空");
        }
        String certPath = devConfigApi.getValueByKey(SNOWY_PAY_ALI_CERT_PATH_KEY);
        if(ObjectUtil.isEmpty(certPath)) {
            throw new CommonException("支付宝支付参数未正确配置：certPath为空");
        }
        String rootCertPath = devConfigApi.getValueByKey(SNOWY_PAY_ALI_ROOT_CERT_PATH_KEY);
        if(ObjectUtil.isEmpty(rootCertPath)) {
            throw new CommonException("支付宝支付参数未正确配置：rootCertPath为空");
        }
        String appCertPath = devConfigApi.getValueByKey(SNOWY_PAY_ALI_APP_CERT_PATH_KEY);
        if(ObjectUtil.isEmpty(appCertPath)) {
            throw new CommonException("支付宝支付参数未正确配置：appCertPath为空");
        }
        String serverUrl = devConfigApi.getValueByKey(SNOWY_PAY_ALI_SERVER_URL_KEY);
        if(ObjectUtil.isEmpty(serverUrl)) {
            throw new CommonException("支付宝支付参数未正确配置：serverUrl为空");
        }
        String notifyUrl = devConfigApi.getValueByKey(SNOWY_PAY_ALI_NOTIFY_URL_KEY);
        if(ObjectUtil.isEmpty(notifyUrl)) {
            throw new CommonException("支付宝支付参数未正确配置：notifyUrl为空");
        }
        try {
            return AliPayApiConfig.builder().setAppId(appId)
                    .setPrivateKey(privateKey)
                    .setAliPayCertPath(certPath)
                    .setAliPayRootCertPath(rootCertPath)
                    .setAppCertPath(appCertPath)
                    .setCharset(CharsetUtil.UTF_8)
                    .setServiceUrl(serverUrl)
                    .setSignType(AlipayConstants.SIGN_TYPE_RSA2)
                    .setExParams(JSONUtil.createObj().set("mchUserId", mchUserId).set("notifyUrl", notifyUrl))
                    .buildByCert();
        } catch (AlipayApiException alipayApiException) {
            throw new CommonException("支付宝支付参数配置存在错误，原因：{}", alipayApiException.getMessage());
        }
    }

    /**
     * 获取微信支付配置
     *
     * @author xuyuxiang
     * @date 2022/8/16 14:08
     **/
    public static WxPayConfig getWxPayApiConfig() {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        String appId = devConfigApi.getValueByKey(SNOWY_PAY_WX_APP_ID_KEY);
        if(ObjectUtil.isEmpty(appId)) {
            throw new CommonException("微信支付参数未正确配置：appId为空");
        }
        String appSecret = devConfigApi.getValueByKey(SNOWY_PAY_WX_APP_SECRET_KEY);
        if(ObjectUtil.isEmpty(appSecret)) {
            throw new CommonException("微信支付参数未正确配置：appSecret为空");
        }
        String mchId = devConfigApi.getValueByKey(SNOWY_PAY_WX_MCH_ID_KEY);
        if(ObjectUtil.isEmpty(mchId)) {
            throw new CommonException("微信支付参数未正确配置：mchId为空");
        }
        String mchKey = devConfigApi.getValueByKey(SNOWY_PAY_WX_MCH_KEY_KEY);
        if(ObjectUtil.isEmpty(mchKey)) {
            throw new CommonException("微信支付参数未正确配置：mchKey为空");
        }
        String keyPath = devConfigApi.getValueByKey(SNOWY_PAY_WX_KEY_PATH_KEY);
        if(ObjectUtil.isEmpty(keyPath)) {
            throw new CommonException("微信支付参数未正确配置：keyPath为空");
        }
        String privateKeyPath = devConfigApi.getValueByKey(SNOWY_PAY_WX_PRIVATE_KEY_PATH_KEY);
        if(ObjectUtil.isEmpty(privateKeyPath)) {
            throw new CommonException("微信支付参数未正确配置：privateKeyPath为空");
        }
        String privateCertPath = devConfigApi.getValueByKey(SNOWY_PAY_WX_PRIVATE_CERT_PATH_KEY);
        if(ObjectUtil.isEmpty(privateCertPath)) {
            throw new CommonException("微信支付参数未正确配置：privateCertPath为空");
        }
        String certSerialNo = devConfigApi.getValueByKey(SNOWY_PAY_WX_CERT_SERIAL_NO_KEY);
        if(ObjectUtil.isEmpty(certSerialNo)) {
            throw new CommonException("微信支付参数未正确配置：certSerialNo为空");
        }
        String apiV3Key = devConfigApi.getValueByKey(SNOWY_PAY_WX_API_V3_KEY_KEY);
        if(ObjectUtil.isEmpty(apiV3Key)) {
            throw new CommonException("微信支付参数未正确配置：apiV3Key为空");
        }
        String notifyUrl = devConfigApi.getValueByKey(SNOWY_PAY_WX_NOTIFY_URL_KEY);
        if(ObjectUtil.isEmpty(notifyUrl)) {
            throw new CommonException("微信支付参数未正确配置：notifyUrl为空");
        }
        String refundNotifyUrl = devConfigApi.getValueByKey(SNOWY_PAY_WX_REFUND_NOTIFY_URL_KEY);
        if(ObjectUtil.isEmpty(refundNotifyUrl)) {
            throw new CommonException("微信支付参数未正确配置：refundNotifyUrl为空");
        }
        try {
            WxPayConfig wxPayConfig = new WxPayConfig();
            wxPayConfig.setAppId(appId);
            wxPayConfig.setMchId(mchId);
            wxPayConfig.setMchKey(mchKey);
            wxPayConfig.setKeyPath(keyPath);
            wxPayConfig.setPrivateKeyPath(privateKeyPath);
            wxPayConfig.setPrivateCertPath(privateCertPath);
            wxPayConfig.setCertSerialNo(certSerialNo);
            wxPayConfig.setApiV3Key(apiV3Key);
            wxPayConfig.setNotifyUrl(notifyUrl);
            // 微信配置微信公众号AppSecret，此处设置到WxPayApi的属性
            WxPayApi.WX_WP_APP_SECRET = appSecret;
            // 微信配置无退款回调地址，此处设置到WxPayApi的属性
            WxPayApi.REFUND_NOTIFY_URL = refundNotifyUrl;
            return wxPayConfig;
        } catch (Exception exception) {
            throw new CommonException("微信支付参数配置存在错误，原因：{}", exception.getMessage());
        }
    }

    /**
     * API文档分组配置
     *
     * @author xuyuxiang
     * @date 2022/7/7 16:18
     **/
    @Bean(value = "payDocApi")
    public Docket payDocApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("支付功能PAY")
                        .description("支付功能PAY")
                        .termsOfServiceUrl("https://www.xiaonuo.vip")
                        .contact(new Contact("SNOWY_TEAM","https://www.xiaonuo.vip", "xuyuxiang29@foxmail.com"))
                        .version("2.0.0")
                        .build())
                .globalResponseMessage(RequestMethod.GET, CommonResult.responseList())
                .globalResponseMessage(RequestMethod.POST, CommonResult.responseList())
                .groupName("支付功能PAY")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("vip.xiaonuo.pay"))
                .paths(PathSelectors.any())
                .build();
    }
}
