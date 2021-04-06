package com.zykj.yn.boc.coupon.common;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-11-27
 */

public class ServerResponse<T> implements Serializable {

    public static final Integer OK = 200;
    private final int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public static <T> ServerResponse<T> ok() {

        return new ServerResponse<T>(200, "OK", null);
    }

    public static <T> ServerResponse<T> ok(T data) {

        return new ServerResponse<T>(200, "OK", data);
    }

    public static <T> ServerResponse<T> createSuccess(T data) {
        return new ServerResponse<T>(OK, "成功", data);
    }

    public static <T> ServerResponse<T> createMessage(int code, String msg, T data) {
        return new ServerResponse<T>(code, msg, data);
    }

    public static <T> ServerResponse<T> createMessage(int code, String msg) {
        return new ServerResponse<T>(code, msg);
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", getStatus());
        jsonObject.put("msg", getMsg());
        jsonObject.put("data", getData());
        return jsonObject.toString();
    }
}
