package com.zykj.yn.boc.coupon.service;

import com.alibaba.fastjson.JSONObject;
import com.zykj.yn.boc.coupon.common.CouponConfig;
import com.zykj.yn.boc.coupon.pojo.Order;
import com.zykj.yn.boc.coupon.pojo.OrderStateEnum;
import com.zykj.yn.boc.coupon.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 支付
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-25
 */

@Slf4j
@Service
public class PayService {

    private final Pkcs7Util pkcs7Util;
    private final CouponConfig couponConfig;
    private final OrderService orderService;
    private final CouponService couponService;
    private final RedisUtil redisUtil;
    private final InvitationService invitationService;

    public PayService(Pkcs7Util pkcs7Util, CouponConfig couponConfig, OrderService orderService, CouponService couponService, RedisUtil redisUtil, InvitationService invitationService) {
        this.pkcs7Util = pkcs7Util;
        this.couponConfig = couponConfig;
        this.orderService = orderService;
        this.couponService = couponService;
        this.redisUtil = redisUtil;
        this.invitationService = invitationService;
    }

    public String createPayParam(String orderNo, BigDecimal money) {
        String paymount = BigDecimalUtil.format(money);
        String requestTime = DateUtil.getFormat("yyyyMMddHHmmss");
        String merchantNo = couponConfig.getMerchantNo();
        String orderUrl = couponConfig.getCallbackUrl();
        String version = "1.0.1";
        String messageId = "0000212";
        String security = "P7";
        String custBackFlag = "1";
        Map<String, String> map = paramToMap(orderNo, paymount, requestTime, orderUrl);
        String xml = XmlToMapUtil.addXmlHerad(requestTime, XmlToMapUtil.map2xml(map));
        String message = Base64Utils.encodeToString(xml.getBytes(StandardCharsets.UTF_8));
        String sign = pkcs7Util.sign(xml.getBytes(StandardCharsets.UTF_8));
        return createJsonParam(merchantNo, version, messageId, security, custBackFlag, message, sign).toJSONString();


    }

    @NotNull
    private JSONObject createJsonParam(String merchantNo, String version, String messageId, String security, String custBackFlag, String message, String sign) {
        JSONObject json = new JSONObject();
        json.put("merchantNo", Base64Utils.encodeToString(merchantNo.getBytes(StandardCharsets.UTF_8)));
        json.put("version", Base64Utils.encodeToString(version.getBytes(StandardCharsets.UTF_8)));
        json.put("messageId", Base64Utils.encodeToString(messageId.getBytes(StandardCharsets.UTF_8)));
        json.put("security", Base64Utils.encodeToString(security.getBytes(StandardCharsets.UTF_8)));
        json.put("custBackFlag", custBackFlag);
        json.put("message", message);
        json.put("signature", sign);
        return json;
    }


    private Map<String, String> paramToMap(String orderNo, String paymount, String requestTime, String orderUrl) {
        Map<String, String> map = new HashMap<>(16);
        map.put("orderNo", orderNo);
        map.put("curCode", "001");
        map.put("orderAmount", paymount);
        map.put("orderTime", requestTime);
        map.put("orderNote", "众耘科技");
        map.put("subMerchantName", "众耘科技");
        map.put("orderUrl", orderUrl);
        map.put("subMerchantCode", "OF0001");
        map.put("subMerchantClass", "0001");
        map.put("subMerchantZone", "cz340000");
        return map;

    }

    public synchronized void paySuccess(String orderNo, String payTime, String cardTyp, String orderSeq, String merchantNo) {
        Optional<Order> orderOptional = orderService.findByOrderNoAndState(orderNo, OrderStateEnum.UNPAID);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setPayTime(payTime);
            order.setCardTyp(cardTyp);
            order.setMerchantNo(merchantNo);
            order.setOrderSeq(orderSeq);
            order.setState(OrderStateEnum.PAID);
            order = orderService.saveAndFlush(order);
            BigDecimal money = new BigDecimal("5.00");
            redisUtil.delKey("user:"+order.getUserId()+":order:pay:" + orderNo);
            couponService.addCoupon(order.getUserId(), order.getOrderId(), money, 4);
            invitationService.updateAcceptState(order.getUserId());
        }
    }


}
