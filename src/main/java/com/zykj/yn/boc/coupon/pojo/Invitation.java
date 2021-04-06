package com.zykj.yn.boc.coupon.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 邀请
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-04-01
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "yn_boc_invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer inviterId;

    /**
     * 邀请者 手机号
     */
    private String inviterPhone;


    private Integer inviteeId;

    /**
     * 被邀请者 手机号
     */
    private String inviteePhone;

    private LocalDateTime inviteTime;

    private InvitationStateEnum state;

    private BigDecimal money;

    private Integer month;

    private Integer year;
}
