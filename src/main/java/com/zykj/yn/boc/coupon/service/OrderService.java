package com.zykj.yn.boc.coupon.service;

import com.zykj.yn.boc.coupon.dao.OrderRepository;
import com.zykj.yn.boc.coupon.pojo.Order;
import com.zykj.yn.boc.coupon.pojo.OrderStateEnum;
import com.zykj.yn.boc.coupon.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 订单
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-25
 */
@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RedisUtil redisUtil;

    public OrderService(OrderRepository orderRepository, RedisUtil redisUtil) {
        this.orderRepository = orderRepository;
        this.redisUtil = redisUtil;
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> findByUserIdAndOrderNo(int userId, String orderNo) {
        return orderRepository.findByUserIdAndOrderNo(userId, orderNo);
    }

    public Optional<Order> findByOrderNoAndState(String orderNo, OrderStateEnum state) {
        return orderRepository.findByOrderNoAndState(orderNo, state);
    }

    public Order saveAndFlush(Order order) {
        return orderRepository.saveAndFlush(order);
    }

    public Optional<Order> findByUserIdAndMonth(int userId, int month) {
        return orderRepository.findByUserIdAndMonth(userId, month);
    }

    public void deleteByOrderNo(String orderNo) {
        Optional<Order> orderOptional = orderRepository.findByOrderNo(orderNo);
        orderOptional.ifPresent(order -> {
            redisUtil.delKey("user:" + order.getUserId() + ":order:pay:" + orderNo);
            orderRepository.deleteByOrderNo(orderNo);
        });


    }
}
