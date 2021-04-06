package com.zykj.yn.boc.coupon.pojo;

import lombok.Data;

import javax.persistence.*;
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
@Entity
@Table(name = "yn_boc_coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponId;

    private LocalDateTime createDate;

    private LocalDateTime updateTime;

    private Integer month;

    private BigDecimal money;

    private Integer userId;

    private Integer orderId;

    private CouponStateEnum state;

    private String code;

    private String link;

    private Integer applyStatus;



}
