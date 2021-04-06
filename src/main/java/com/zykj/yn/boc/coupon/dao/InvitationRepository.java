package com.zykj.yn.boc.coupon.dao;

import com.zykj.yn.boc.coupon.pojo.Invitation;
import com.zykj.yn.boc.coupon.pojo.InvitationStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author tang
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation,Integer> {
    List<Invitation> findAllByInviterId(int userId);

    /**
     * 被邀请人手机号查询
     * @param mobile 手机号
     * @return 被邀请信息
     */
    Invitation findByInviteePhone(String mobile);

    /**
     * 邀请人手机号指定月份查询
     * @param mobile 手机号
     * @param month 月份
     * @return 邀请数据
     */
    List<Invitation> findByInviterPhoneAndMonth(String mobile, int month);

    /**
     * 查询邀请人月份充值总金额
     * @param userId 邀请人
     * @param stateEnum 状态
     * @param month 月份
     * @return 金额
     */
    @Query(value = "SELECT SUM(invit.money) FROM Invitation AS invit WHERE invit.inviterId = ?1 AND invit.state = ?2 AND invit.month = ?3")
    Optional<BigDecimal> findSumMoneyByInviterIdAndStateAndMonth(int userId, InvitationStateEnum stateEnum, int month);
}
