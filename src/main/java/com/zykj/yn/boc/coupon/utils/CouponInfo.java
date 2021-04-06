package com.zykj.yn.boc.coupon.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 立减金信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponInfo {

    private Integer status;

    private String code;

    private String link;
}
