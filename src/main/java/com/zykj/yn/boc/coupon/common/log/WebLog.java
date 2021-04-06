package com.zykj.yn.boc.coupon.common.log;

import java.lang.annotation.*;

/**
 * @author tang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface WebLog {

    String value() default "";

}
