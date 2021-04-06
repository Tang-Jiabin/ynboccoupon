package com.zykj.yn.boc.coupon.controller;

import com.zykj.yn.boc.coupon.common.ServerResponse;
import com.zykj.yn.boc.coupon.common.interceptor.Authorization;
import com.zykj.yn.boc.coupon.common.log.WebLog;
import com.zykj.yn.boc.coupon.pojo.Coupon;
import com.zykj.yn.boc.coupon.service.CouponService;
import com.zykj.yn.boc.coupon.utils.DateUtil;
import com.zykj.yn.boc.coupon.utils.HFUtils;
import com.zykj.yn.boc.coupon.vo.CouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 立减金
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-26
 */

@Slf4j
@RestController
@RequestMapping("coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Authorization
    @WebLog("查询立减金金额")
    @GetMapping("/getCouponMoney")
    public ServerResponse<String> getCouponMoney(@RequestAttribute String token) throws Exception {
        int userId = Integer.parseInt(token);
        Optional<BigDecimal> moneyOptional = couponService.getCouponMoney(userId);
        BigDecimal money = moneyOptional.orElse(new BigDecimal("0.00"));
        return ServerResponse.ok(money.toString());
    }

    @Authorization
    @WebLog("查询指定月份立减金")
    @GetMapping("/getCouponByMonth")
    public ServerResponse<List<CouponVO>> getCouponByMonth(Integer month, @RequestAttribute String token) {
        int userId = Integer.parseInt(token);
        List<Coupon> couponList = couponService.getCouponByMonth(userId, month);
        List<CouponVO> couponVOList = new ArrayList<>();
        couponList.forEach(coupon -> couponVOList.add(CouponVO.builder().money(coupon.getMoney()).createDate(DateUtil.getFormat(coupon.getCreateDate())).state(coupon.getState()).link(coupon.getLink()).build()));
        return ServerResponse.ok(couponVOList);
    }

}
