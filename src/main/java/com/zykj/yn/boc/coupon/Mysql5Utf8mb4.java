package com.zykj.yn.boc.coupon;

import org.hibernate.dialect.MySQL5Dialect;

/**
 * utf8mb4
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-08
 */

public class Mysql5Utf8mb4 extends MySQL5Dialect {
    @Override
    public String getTableTypeString() {
        return "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci";
    }
}
