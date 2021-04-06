package com.zykj.yn.boc.coupon.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zykj.yn.boc.coupon.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import org.jetbrains.annotations.NotNull;

/**
 * 立减金
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-31
 */
@Slf4j
public class CouponUtil {

    private static final String ERROR_LINK = "https://wx.zykjhh.com/prize_platform/wxljj/error.html";
    private static final String DOMAIN_LINK = "https://wx.zykjhh.com/prize_platform/v1/gotoOAuth?getCode=";
    private static final String REQUEST_LINK = "https://wx.zykjhh.com/prize_platform/v1/getCoupon";
    private static final String CALLBACK = "https://ynwechat.zhongyunkj.cn/ynboccoupon/";
    private static final String KEY = "mFFeSyxUPjjtrEIZyHZ0Gw==";


    public static CouponInfo getCoupon() {
        String requestData = buildRequestData();
        String strResult = request(requestData);
        JSONObject json = buildResult(strResult);
        return CouponInfo.builder().status(json.getInteger("status")).code(json.getString("code")).link(json.getString("link")).build();
    }

    private static JSONObject buildResult(String strResult) {
        JSONObject json = JSON.parseObject(strResult);
        Integer status = json.getInteger("status");
        String data = json.getString("data");
        String msg = json.getString("msg");
        String link = DOMAIN_LINK + data;
        json = getJsonResult(status, data, msg, link);
        return json;
    }

    @NotNull
    private static JSONObject getJsonResult(Integer status, String data, String msg, String link) {
        JSONObject json = new JSONObject();
        if (ServerResponse.OK.equals(status)) {
            json.put("status", 200);
            json.put("code", data);
            json.put("link", link);
        } else {
            json.put("status", 400);
            json.put("code", msg);
            json.put("link", ERROR_LINK);
        }
        return json;
    }

    private static String buildRequestData() {
        JSONObject json = new JSONObject();
        json.put("prizeType", 1);
        json.put("amount", 1);
        json.put("callback", CouponUtil.CALLBACK);
        json.put("voucherId", "3");
        return encode(json.toJSONString());
    }

    private static String request(String requestData) {
        FormBody body = new FormBody.Builder()
                .add("activityNo", "3")
                .add("data", requestData).build();
        return OkhttpUtil.getInstance().httpPost(REQUEST_LINK, body);
    }

    private static String encode(String jsonStr) {
        String enStr = "";
        try {
            enStr = AESUtil.encryptToString(jsonStr, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("立减金领取加密失败！");
        }
        return enStr;
    }
}
