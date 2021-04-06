package com.zykj.yn.boc.coupon.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 手机号
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */

public class MobileUtil {

    private static final int PHONE_LENGTH = 11;
    private static final Pattern PATTERN = Pattern.compile("0?(13|14|15|16|17|18|19)[0-9]{9}");

    public static boolean validation(String phone) {
        return !StringUtils.hasText(phone) || !PATTERN.matcher(phone).matches();
    }

    public static String midReplaceStar(String phone) {
        return !StringUtils.hasText(phone) || phone.length() != PHONE_LENGTH ? "" : phone.replace(phone.substring(3, 7), "****");
    }


}
