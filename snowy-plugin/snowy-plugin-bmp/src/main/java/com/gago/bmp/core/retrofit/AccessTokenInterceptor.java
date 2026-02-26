package com.gago.bmp.core.retrofit;

import cn.hutool.core.util.StrUtil;
import com.gago.bmp.service.BmpService;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 拦截Retrofit请求并在Header中加入AccessToken
 */
public class AccessTokenInterceptor implements Interceptor {

    private final BmpService bmpService;

    public AccessTokenInterceptor(BmpService bmpService) {
        this.bmpService = bmpService;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (StrUtil.containsIgnoreCase(chain.request().url().toString(), "/api/v1/gago/auth/token")) {
            return chain.proceed(chain.request());
        }
        Request newReq = chain.request().newBuilder()
                .header("token", bmpService.getAccessToken().getAccessToken()).build();
        return chain.proceed(newReq);
    }
}
