package vip.xiaonuo.common.retrofit;

import cn.hutool.core.util.StrUtil;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * 自定义Retrofit转换器，适应多种响应体
 */
public class AggregativeConverterFactory extends Converter.Factory {

    /**
     * 内部对于JSON格式的处理依然使用Jackson转换器
     */
    private final JacksonConverterFactory jacksonConverterFactory;

    private final ByteConverter byteConverter;

    public AggregativeConverterFactory(JacksonConverterFactory jacksonConverterFactory) {
        this.jacksonConverterFactory = jacksonConverterFactory;
        byteConverter = new ByteConverter();
    }

    public static AggregativeConverterFactory create(JacksonConverterFactory jacksonConverterFactory) {
        return new AggregativeConverterFactory(jacksonConverterFactory);
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        // 获取二进制的文件等数据，直接返回byte[]
        if (StrUtil.equalsAny(type.getTypeName(), "byte[]")) {
            return byteConverter;
        } else {
            return jacksonConverterFactory.responseBodyConverter(type, annotations, retrofit);
        }
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return jacksonConverterFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    private static class ByteConverter implements Converter<ResponseBody, byte[]> {

        @Nullable
        @Override
        public byte[] convert(ResponseBody value) throws IOException {
            return value.bytes();
        }
    }
}
