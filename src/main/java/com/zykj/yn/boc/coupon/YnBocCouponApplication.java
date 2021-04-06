package com.zykj.yn.boc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author tang
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class YnBocCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(YnBocCouponApplication.class, args);
    }

}
