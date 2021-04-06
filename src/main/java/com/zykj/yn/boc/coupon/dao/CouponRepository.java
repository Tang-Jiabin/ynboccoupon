package com.zykj.yn.boc.coupon.dao;

import com.zykj.yn.boc.coupon.pojo.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.nio.file.OpenOption;
import java.util.List;
import java.util.Optional;

/**
 * @author tang
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon,Integer> {

    /**
     * 查询总金额
     * @param userId  用户
     * @return 金额
     */
    @Query(value = "SELECT SUM(money) FROM Coupon WHERE userId = ?1")
    Optional<BigDecimal> getCouponMoney(int userId);

    /**
     * 根据月份查询立减金
     * @param userId 用户
     * @param month 月份
     * @return 立减金
     */
    List<Coupon> findAllByUserIdAndMonth(int userId, Integer month);
}
