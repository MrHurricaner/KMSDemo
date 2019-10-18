package com.kms.appcore.apiservice;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author matrixelement
 */
public class MultipleUrlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request origin = chain.request();
        RequestInfo reqInfo = RequestInfo.create(origin.url().toString());
        Request request = origin.newBuilder()
                .url(reqInfo.getRealUrl())
                .method(origin.method(), origin.body())
                .build();
        return chain.proceed(request);
    }
}
