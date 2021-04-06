package com.zykj.yn.boc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zykj.yn.boc.coupon.common.CouponConfig;
import com.zykj.yn.boc.coupon.common.ServerResponse;
import com.zykj.yn.boc.coupon.common.interceptor.Authorization;
import com.zykj.yn.boc.coupon.common.interceptor.TokenModel;
import com.zykj.yn.boc.coupon.common.log.WebLog;
import com.zykj.yn.boc.coupon.pojo.Invitation;
import com.zykj.yn.boc.coupon.pojo.User;
import com.zykj.yn.boc.coupon.service.InvitationService;
import com.zykj.yn.boc.coupon.service.UserService;
import com.zykj.yn.boc.coupon.utils.MobileUtil;
import com.zykj.yn.boc.coupon.utils.OkhttpUtil;
import com.zykj.yn.boc.coupon.utils.RedisUtil;
import com.zykj.yn.boc.coupon.utils.RlySmsUtil;
import com.zykj.yn.boc.coupon.vo.InvitationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-24
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {


    @Resource
    private HttpServletResponse response;

    private final UserService userService;
    private final RedisUtil redisUtil;
    private final CouponConfig couponConfig;
    private final InvitationService invitationService;

    public UserController(UserService userService, RedisUtil redisUtil, CouponConfig couponConfig, InvitationService invitationService) {
        this.userService = userService;
        this.redisUtil = redisUtil;
        this.couponConfig = couponConfig;
        this.invitationService = invitationService;
    }


    @WebLog("手机银行登录")
    @PostMapping("bocLogin")
    public ServerResponse<String> bocLogin(String data) {
        JSONObject auInfo = userService.getAuthorizeInfo(data);
        if (auInfo == null) {
            return ServerResponse.createMessage(410, "请重新授权");
        }
        User bocUserInfo = userService.bocInfoToDto(auInfo);
        if (!StringUtils.hasText(bocUserInfo.getMobile())) {
            return ServerResponse.createMessage(410, "请重新授权");
        }
        Optional<User> userOptional = userService.findByMobile(bocUserInfo.getMobile());
        User user = userOptional.orElseGet(() -> userService.save(bocUserInfo));
        userService.update(user, bocUserInfo);
        TokenModel token = userService.createToken(user);
        if (StringUtils.hasText(token.getToken())) {
            return ServerResponse.ok(token.getToken());
        }
        return ServerResponse.createMessage(410, "请重新授权");
    }


    @WebLog("获取验证码")
    @GetMapping("getCode")
    public ServerResponse<String> getCode(String mobile) {
        if (MobileUtil.validation(mobile)) {
            return ServerResponse.createMessage(410, "手机号错误");
        }
        String code = redisUtil.getString("user:sms:" + mobile);
        if (StringUtils.hasText(code)) {
            redisUtil.setStringSeconds("user:sms:" + mobile, code, 60 * 10L);
            return ServerResponse.ok();
        }
        code = String.valueOf(new SecureRandom().nextInt(899999) + 100000);
        boolean send = RlySmsUtil.send(mobile, new String[]{code, "10分钟"});
        if (send) {
            redisUtil.setStringSeconds("user:sms:" + mobile, code, 60 * 10L);
            return ServerResponse.ok();
        }
        return ServerResponse.createMessage(411, "发送失败");
    }

    @WebLog("手机号登录")
    @PostMapping("mobileLogin")
    public ServerResponse<String> mobileLogin(String mobile, String code, String openId) {
        String reCode = redisUtil.getString("user:sms:" + mobile);
        if (!code.equals(reCode)) {
            return ServerResponse.createMessage(410, "验证码错误");
        }
        redisUtil.delKey("user:sms:" + mobile);
        Optional<User> userOptional = userService.findByMobile(mobile);
        User build = User.builder().mobile(mobile).openId(openId).createTime(LocalDateTime.now()).build();
        User user = userOptional.orElseGet(() -> userService.save(build));
        user = userService.update(user, build);
        TokenModel token = userService.createToken(user);
        return ServerResponse.ok(token.getToken());
    }


    @WebLog("微信授权回调")
    @RequestMapping("wxCallback")
    public ServerResponse<String> wxCallback(String code, String state) {
        String path = "wx_login.html";
        String url = getAuthUrl(code);
        String result = OkhttpUtil.getInstance().httpGet(url);
        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult == null) {
            return ServerResponse.createMessage(400, "获取授权信息失败");
        }
        String openid = jsonResult.getString("openid");
        if (StringUtils.hasText(openid)) {
            Optional<User> userOptional = userService.findByOpenId(openid);
            AtomicBoolean re = new AtomicBoolean(false);
            userOptional.ifPresent(user -> {
                if (StringUtils.hasText(user.getMobile()) && StringUtils.hasText(user.getOpenId())) {
                    TokenModel token = userService.createToken(user);
                    if (StringUtils.hasText(token.getToken())) {
                        wxLoginRedirect(token);
                        re.set(true);
                    }
                }
            });
            if (re.get()) {
                return ServerResponse.ok();
            }
        }
        Map<String, String> map = new HashMap<>(16);
        map.put("openId", openid);
        redirect(couponConfig.getWxDomain() + path, map);
        return ServerResponse.ok();
    }

    @Authorization
    @WebLog("获取邀请信息")
    @GetMapping("getInvitationInfo")
    public ServerResponse<InvitationVO> getInvitationInfo(@RequestAttribute String token) {
        int userId = Integer.parseInt(token);
        List<Invitation> invitationList = invitationService.getInviteListByUserId(userId);
        InvitationVO invitationVO = InvitationVO.builder().money(new BigDecimal("0.00")).build();
        invitationList.forEach(invite -> {
            invitationVO.setMoney(invitationVO.getMoney().add(invite.getMoney()));
            invite.setInviteePhone(MobileUtil.midReplaceStar(invite.getInviteePhone()));
        });
        invitationVO.setNumber(invitationList.size());
        invitationVO.setInvitationList(invitationList);
        invitationVO.setShare(userId);
        return ServerResponse.ok(invitationVO);
    }

    @WebLog("接受邀请")
    @PostMapping("acceptInvitation")
    public ServerResponse<String> acceptInvitation(String share, String phone) {
        int userId = Integer.parseInt(share);
        if (MobileUtil.validation(phone)) {
            return ServerResponse.createMessage(410, "手机号格式错误");
        }
        Optional<User> userOptional = userService.findByUserId(userId);
        userOptional.ifPresent(user -> invitationService.add(user,phone));
        return ServerResponse.ok();
    }


    private String getAuthUrl(String code) {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + couponConfig.getAppId() + "&secret=" + couponConfig.getAppSecret() + "&code=" +
                code + "&grant_type=authorization_code";
    }

    private void redirect(String domain, Map<String, String> param) {
        try {
            String s = OkhttpUtil.buildUrl(domain, param);
            response.sendRedirect(s);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("重定向失败：{}", domain);
        }
    }

    private void wxLoginRedirect(TokenModel token) {
        Map<String, String> map = new HashMap<>(16);
        map.put("yn_boc_coupon_token", token.getToken());
        String path = "wx_my_coupon.html";
        redirect(couponConfig.getWxDomain() + path, map);
    }


}



