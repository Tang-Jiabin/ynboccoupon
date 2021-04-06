package com.zykj.yn.boc.coupon.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-24
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "yn_boc_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    /**
     * 客户号
     */
    private String customerId;

    /**
     * 微信openid
     */
    private String openId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 机构号
     */
    private String branchId;
    /**
     * 省联行号
     */
    private String ibknum;

    /**
     * 创建日期
     */
    private LocalDateTime createTime;
    /**
     * 更新日期
     */
    private LocalDateTime updateTime;


}
