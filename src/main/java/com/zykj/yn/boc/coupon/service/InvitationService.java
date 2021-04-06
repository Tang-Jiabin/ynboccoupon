package com.zykj.yn.boc.coupon.service;

import com.zykj.yn.boc.coupon.common.SnowflakeIdFactory;
import com.zykj.yn.boc.coupon.dao.InvitationRepository;
import com.zykj.yn.boc.coupon.dao.PrepaidRefillRepository;
import com.zykj.yn.boc.coupon.pojo.Invitation;
import com.zykj.yn.boc.coupon.pojo.InvitationStateEnum;
import com.zykj.yn.boc.coupon.pojo.PrepaidRefill;
import com.zykj.yn.boc.coupon.pojo.User;
import com.zykj.yn.boc.coupon.utils.HFUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.zykj.yn.boc.coupon.pojo.InvitationStateEnum.*;

/**
 * 邀请
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-04-01
 */
@Slf4j
@Service
public class InvitationService {


    private final InvitationRepository invitationRepository;
    private final UserService userService;
    private final PrepaidRefillRepository prepaidRefillRepository;


    public InvitationService(InvitationRepository invitationRepository, UserService userService, PrepaidRefillRepository prepaidRefillRepository) {
        this.invitationRepository = invitationRepository;
        this.userService = userService;
        this.prepaidRefillRepository = prepaidRefillRepository;
    }

    public List<Invitation> getInviteListByUserId(int userId) {
        return invitationRepository.findAllByInviterId(userId);
    }

    public Invitation add(User user, String phone) {
        LocalDateTime now = LocalDateTime.now();
        Invitation invitation = Invitation.builder()
                .inviterId(user.getUserId())
                .inviterPhone(user.getMobile())
                .inviteePhone(phone)
                .inviteTime(now)
                .year(now.getYear())
                .month(now.getMonthValue())
                .money(new BigDecimal("2.00"))
                .state(NOT_INVOLVED)
                .build();
        return invitationRepository.save(invitation);
    }

    @Async
    public void updateAcceptState(Integer userId) {
        Optional<User> userOptional = userService.findByUserId(userId);
        BigDecimal[] money = {new BigDecimal("0.00"), new BigDecimal("100.00")};
        userOptional.ifPresent(user -> {
            Invitation invitee = invitationRepository.findByInviteePhone(user.getMobile());
            Integer inviterId = invitee.getInviterId();
            Optional<BigDecimal> sumOptional = invitationRepository.findSumMoneyByInviterIdAndStateAndMonth(inviterId, RECEIVED, LocalDateTime.now().getMonthValue());
            BigDecimal bigDecimal = sumOptional.orElse(money[0]);
            InvitationStateEnum stateEnum = bigDecimal.compareTo(money[1]) < 0 ? rechargeState(invitee) : OVERRUN;
            invitee.setState(stateEnum);
            invitee.setInviteeId(userId);
            invitationRepository.save(invitee);
        });
    }


    @NotNull
    private InvitationStateEnum rechargeState(Invitation invitation) {
        SnowflakeIdFactory factory = new SnowflakeIdFactory(3, 3);
        String phone = invitation.getInviterPhone();
        String orderNo = String.valueOf(factory.nextId());
        String moneyStr = String.valueOf(invitation.getMoney().intValue());
        int state = HFUtils.submitOrder(phone, orderNo, moneyStr, HFUtils.hfUrl, HFUtils.szAgentId, HFUtils.key);
        PrepaidRefill prepaidRefill = PrepaidRefill.builder().state(String.valueOf(state)).money(moneyStr).orderNo(orderNo).phone(phone).time(LocalDateTime.now()).build();
        prepaidRefillRepository.save(prepaidRefill);
        return state == 0 ? RECEIVED : INVOLVED;
    }
}
