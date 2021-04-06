package com.zykj.yn.boc.coupon.dao;

import com.zykj.yn.boc.coupon.pojo.Order;
import com.zykj.yn.boc.coupon.pojo.OrderStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author tang
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * 用户月份状态
     *
     * @param userId 用户
     * @param month  月份
     * @param state  状态
     * @return 订单
     */
    Optional<Order> findByUserIdAndMonthAndState(int userId, int month, OrderStateEnum state);

    /**
     * 订单编号查询
     *
     * @param orderNo 订单号
     * @return 订单
     */
    Optional<Order> findByOrderNo(String orderNo);


    /**
     * 用户订单号查询
     *
     * @param userId  用户
     * @param orderNo 订单号
     * @return 订单
     */
    Optional<Order> findByUserIdAndOrderNo(int userId, String orderNo);

    /**
     * 订单状态
     *
     * @param orderNo 订单号
     * @param state   状态
     * @return 订单
     */
    Optional<Order> findByOrderNoAndState(String orderNo, OrderStateEnum state);

    /**
     * 用户月份查询
     *
     * @param userId 用户
     * @param month  月份
     * @return 订单
     */
    Optional<Order> findByUserIdAndMonth(int userId, int month);

    /**
     * 订单号删除
     *
     * @param orderNo 订单号
     */
    void deleteByOrderNo(String orderNo);
}
