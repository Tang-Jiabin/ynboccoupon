package com.zykj.yn.boc.coupon.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "yn_boc_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    private Integer userId;

    private String orderNo;

    private LocalDateTime createDate;

    private Integer month;

    private Integer year;

    private OrderStateEnum state;

    private BigDecimal money;

    private String payTime;

    private String cardTyp;

    private String orderSeq;

    private String merchantNo;

}
