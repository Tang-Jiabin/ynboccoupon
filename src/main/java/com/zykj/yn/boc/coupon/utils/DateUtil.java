package com.zykj.yn.boc.coupon.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具
 *
 * @author : J.Tang
 * @version : v1.0
 * @email : seven_tjb@163.com
 * @date : 2018-10-29
 **/

public class DateUtil {


    public static String getFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getFormat(Date date) {
        return getFormat(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getFormat(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getFormat(String format) {
        return getFormat(new Date(), format);
    }

    public static Date getDate(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        long time = 1619971199;
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofHours(8));
        System.out.println(getFormat(dateTime));
    }

}
