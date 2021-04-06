package com.zykj.yn.boc.coupon.controller;

import com.alibaba.fastjson.JSONObject;
import com.zykj.yn.boc.coupon.common.CouponConfig;
import com.zykj.yn.boc.coupon.common.ServerResponse;
import com.zykj.yn.boc.coupon.common.SnowflakeIdFactory;
import com.zykj.yn.boc.coupon.common.interceptor.Authorization;
import com.zykj.yn.boc.coupon.common.log.WebLog;
import com.zykj.yn.boc.coupon.pojo.Order;
import com.zykj.yn.boc.coupon.pojo.OrderStateEnum;
import com.zykj.yn.boc.coupon.service.OrderService;
import com.zykj.yn.boc.coupon.service.PayService;
import com.zykj.yn.boc.coupon.utils.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@RestController
@RequestMapping("pay")
public class PayController {

    private static final SnowflakeIdFactory ID_FACTORY = new SnowflakeIdFactory(10, 10);
    private static final String ORDER_SUCCESS_STATE = "1";
    private static final String ACTION = "https://ebspay.boc.cn/PGWPortal/QueryOrderTrans.do";

    private final PayService payService;
    private final OrderService orderService;
    private final RedisUtil redisUtil;
    private final Pkcs7Util pkcs7Util;
    private final CouponConfig couponConfig;

    public PayController(PayService payService, OrderService orderService, RedisUtil redisUtil, Pkcs7Util pkcs7Util, CouponConfig couponConfig) {
        this.payService = payService;
        this.orderService = orderService;
        this.redisUtil = redisUtil;
        this.pkcs7Util = pkcs7Util;
        this.couponConfig = couponConfig;
    }

    @Authorization
    @WebLog("手机银行支付")
    @PostMapping("getPayParam")
    public ServerResponse<JSONObject> getPayParam(@RequestAttribute String token) {
        Order order;
        String orderNo;
        BigDecimal money;
        String payParam;
        int userId = Integer.parseInt(token);
        LocalDate date = LocalDate.now();
        Optional<Order> orderOptional = orderService.findByUserIdAndMonth(userId, date.getMonthValue());
        if (orderOptional.isPresent()) {
            order = orderOptional.get();
            if (order.getState().equals(OrderStateEnum.PAID)) {
                return ServerResponse.createMessage(411, "本月已经购买");
            }
            orderNo = order.getOrderNo();
            money = order.getMoney();
            payParam = redisUtil.getString("user:"+userId+":order:pay:" + order.getOrderNo());
            if (!StringUtils.hasText(payParam)) {
                payParam = payService.createPayParam(orderNo, money);
                redisUtil.setString("user:"+userId+":order:pay:" + order.getOrderNo(), payParam, 60 * 60L * 2);
            }
        } else {
            money = new BigDecimal("10.00");
            orderNo = String.valueOf(ID_FACTORY.nextId());
            LocalDateTime dateTime = LocalDateTime.now();
            order = Order.builder().userId(userId).orderNo(orderNo).createDate(dateTime).month(dateTime.getMonthValue()).year(dateTime.getYear()).state(OrderStateEnum.UNPAID).money(money).build();
            order = orderService.save(order);
            payParam = payService.createPayParam(orderNo, money);
            redisUtil.setString("user:"+userId+":order:pay:"  + order.getOrderNo(), payParam, 60 * 60L * 2);
        }
        JSONObject json = new JSONObject();
        json.put("orderNo", orderNo);
        json.put("payParam", payParam);
        return ServerResponse.ok(json);
    }

    @WebLog("支付回调")
    @RequestMapping("/payCallback")
    public ServerResponse<String> payCallback(String merchantNo, String orderNo, String orderSeq, String cardTyp, String payTime, String orderStatus, String payAmount, String signData) {
        String plainText = merchantNo + "|" + orderNo + "|" + orderSeq + "|" + cardTyp + "|" + payTime + "|" + orderStatus + "|" + payAmount;
        boolean verify = pkcs7Util.verifySign(signData, plainText);
        if (verify && ORDER_SUCCESS_STATE.equals(orderStatus)) {
            payService.paySuccess(orderNo, payTime, cardTyp, orderSeq, merchantNo);
        } else {
            log.error("支付回调\t验签失败:{}", plainText);
            orderService.deleteByOrderNo(orderNo);
        }
        return ServerResponse.ok();
    }


    @Authorization
    @WebLog("主动查询")
    @GetMapping("/queryPayStatus")
    public ServerResponse<String> selectOrderStatus(String orderNo, @RequestAttribute String token) {
        int userId = Integer.parseInt(token);
        redisUtil.delKey("user:"+userId+":order:pay:"  + orderNo);
        Optional<Order> orderOptional = orderService.findByUserIdAndOrderNo(userId, orderNo);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getState().equals(OrderStateEnum.PAID)) {
                return ServerResponse.ok();
            }
            String plainText = couponConfig.getMerchantNo() + ":" + order.getOrderNo();
            String sign = pkcs7Util.sign(plainText);
            FormBody body = new FormBody.Builder()
                    .add("merchantNo", couponConfig.getMerchantNo())
                    .add("orderNo", order.getOrderNo())
                    .add("signData", sign).build();
            String s = OkhttpUtil.getInstance().httpPost(ACTION, body);
            String tranStatus = XmlToMapUtil.getXmlStrVal(s).get("tranStatus");
            if (ORDER_SUCCESS_STATE.equals(tranStatus)) {
                payService.paySuccess(order.getOrderNo(), DateUtil.getFormat("yyyyMMddHHmmss"), "10", "0", couponConfig.getMerchantNo());
                return ServerResponse.ok();
            } else {
                log.error("主动查询\t验签失败:{}", plainText);
            }
            orderService.deleteByOrderNo(order.getOrderNo());
        }
        return ServerResponse.createMessage(411, "支付失败");
    }


}
