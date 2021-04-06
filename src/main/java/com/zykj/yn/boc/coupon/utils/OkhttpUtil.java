package com.zykj.yn.boc.coupon.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.platform.Platform;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * okhttp工具类
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-03
 */
@Slf4j
public class OkhttpUtil {


    private volatile static OkhttpUtil mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;
    private Request.Builder builder;


    public static OkhttpUtil getInstance() {
        return initClient(null);
    }

    private OkhttpUtil(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }
        mPlatform = Platform.get();
    }

    private static OkhttpUtil initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkhttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new OkhttpUtil(okHttpClient);
                }
            }
        }
        return mInstance;
    }


    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    public String httpGet(String url) {
        String result = null;

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String httpPost(String url, String data) {
        String result = null;
        RequestBody requestBody = null;
        if (data != null && data.length() > 0) {
            requestBody = RequestBody.Companion.create(data, MediaType.parse("application/json; charset=utf-8"));
        }

        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public String httpPost(String url, FormBody body) {
        String result = null;
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String httpPost(String url, FormBody body, String method) {
        String result = null;
        Request request = new Request.Builder().method(method, body).url(url).post(body).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String buildUrl(String domain, Map<String, String> param) {
        return domain + splicingParam(param);
    }


    private static String splicingParam(Map<String, String> param) {
        return CollectionUtils.isEmpty(param) ? "" : getParam(param);
    }

    private static String getParam(Map<String, String> param) {
        StringBuilder builder = new StringBuilder("?");
        param.forEach((k, v) -> builder.append(k).append("=").append(v).append("&"));
        return builder.substring(0, builder.length() - 1);
    }

    private static String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("encode error : {}", e.getLocalizedMessage());
            return str;
        }
    }

}
