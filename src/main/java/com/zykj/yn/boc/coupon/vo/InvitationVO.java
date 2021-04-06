package com.zykj.yn.boc.coupon.vo;

import com.zykj.yn.boc.coupon.pojo.Invitation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 邀请
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-04-01
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationVO {

    private List<Invitation> invitationList;

    private Integer number;

    private BigDecimal money;

    private Integer share;
}
