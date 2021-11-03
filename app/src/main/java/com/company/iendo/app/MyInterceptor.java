package com.company.iendo.app;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 11:25
 * desc：拦截器  添加header
 */
public class MyInterceptor implements Interceptor {
    private Context mContext;

    public MyInterceptor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
//                .addHeader("device", "android")
//                .addHeader("token", token)
                .build();

        return chain.proceed(request);

    }
}