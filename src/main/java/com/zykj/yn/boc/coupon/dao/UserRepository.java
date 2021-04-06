package com.zykj.yn.boc.coupon.dao;

import com.zykj.yn.boc.coupon.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-24
 */
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    /**
     * 客户号查询
     * @param customerId 客户号
     * @return 用户
     */
    Optional<User> findByCustomerId(String customerId);

    /**
     * openid查询
     * @param openid id
     * @return 用户
     */
    Optional<User> findByOpenId(String openid);

    /**
     * 手机号查询
     * @param mobile 手机号
     * @return 用户
     */
    Optional<User> findByMobile(String mobile);
}
