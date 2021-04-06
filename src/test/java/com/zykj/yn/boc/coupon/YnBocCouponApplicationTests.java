package com.zykj.yn.boc.coupon;

import com.zykj.yn.boc.coupon.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YnBocCouponApplicationTests {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    void contextLoads() throws InterruptedException {


    }

}
