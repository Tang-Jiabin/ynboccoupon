package com.zykj.yn.boc.coupon.vo;

import com.zykj.yn.boc.coupon.pojo.CouponStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 立减金
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponVO {

    private BigDecimal money;

    private String createDate;

    private CouponStateEnum state;

    private String link;
}
