package com.zykj.yn.boc.coupon.service;

import com.zykj.yn.boc.coupon.dao.CouponRepository;
import com.zykj.yn.boc.coupon.pojo.Coupon;
import com.zykj.yn.boc.coupon.pojo.CouponStateEnum;
import com.zykj.yn.boc.coupon.utils.CouponInfo;
import com.zykj.yn.boc.coupon.utils.CouponUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 立减金
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-26
 */
@Slf4j
@Service
public class CouponService {


    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Async
    public void addCoupon(Integer userId, Integer orderId, BigDecimal money) {
        CouponInfo info = CouponUtil.getCoupon();
        LocalDateTime now = LocalDateTime.now();
        Coupon coupon = new Coupon();
        coupon.setCreateDate(now);
        coupon.setUpdateTime(now);
        coupon.setMoney(money);
        coupon.setMonth(now.getMonthValue());
        coupon.setUserId(userId);
        coupon.setOrderId(orderId);
        coupon.setState(CouponStateEnum.NOT_RE);
        coupon.setCode(info.getCode());
        coupon.setLink(info.getLink());
        coupon.setApplyStatus(info.getStatus());
        couponRepository.save(coupon);
    }

    @Async
    public void addCoupon(Integer userId, Integer orderId, BigDecimal money, int number) {
        for (int i = 0; i < number; i++) {
            addCoupon(userId, orderId, money);
        }
    }

    public Optional<BigDecimal> getCouponMoney(int userId) {
        return couponRepository.getCouponMoney(userId);
    }

    public List<Coupon> getCouponByMonth(int userId, Integer month) {
        return couponRepository.findAllByUserIdAndMonth(userId, month);
    }

}
