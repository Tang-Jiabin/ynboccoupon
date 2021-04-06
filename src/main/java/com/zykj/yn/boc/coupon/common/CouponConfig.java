package com.zykj.yn.boc.coupon.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 银行文件配置
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-23
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "coupon")
public class CouponConfig {

    private String merchantNo;

    private String callbackUrl;

    private String keyStorePath;

    private String keystorePwd;

    private String certificatePath;

    private String storePath;

    private String alias;

    private String storePw;

    private String keyPw;

    private String appId;

    private String appSecret;

    private String wxDomain;

    private String bocDomain;

}
