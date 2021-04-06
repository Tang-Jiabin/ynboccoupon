package com.zykj.yn.boc.coupon.service;

import com.alibaba.fastjson.JSONObject;
import com.zykj.yn.boc.coupon.common.interceptor.TokenManager;
import com.zykj.yn.boc.coupon.common.interceptor.TokenModel;
import com.zykj.yn.boc.coupon.dao.UserRepository;
import com.zykj.yn.boc.coupon.pojo.User;
import com.zykj.yn.boc.coupon.utils.CipherUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-24
 */
@Slf4j
@Service
public class UserService {

    private final CipherUtil cipherUtil;
    private final UserRepository userRepository;
    private final TokenManager tokenManager;

    public UserService(CipherUtil cipherUtil, UserRepository userRepository, TokenManager tokenManager) {
        this.cipherUtil = cipherUtil;
        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
    }

    public User bocInfoToDto(JSONObject auInfo) {
        String mBody = auInfo.getString("body");
        String sKey = auInfo.getString("skey");
        mBody = cipherUtil.deaes(mBody, sKey);
        JSONObject bodyData = JSONObject.parseObject(mBody);
        return userJsonToBean(bodyData);
    }

    public User userJsonToBean(JSONObject jsonData) {
        String customerId = jsonData.getString("customerId");
        String mobile = jsonData.getString("mobile");
        jsonData.put("createTime", LocalDateTime.now());
        return StringUtils.hasText(customerId) && StringUtils.hasText(mobile) ? jsonData.toJavaObject(User.class) : new User();
    }


    public User save(User user) {
        return userRepository.save(user);
    }

    public TokenModel createToken(User user) {
        return tokenManager.createToken(String.valueOf(user.getUserId()));
    }

    public JSONObject getAuthorizeInfo(String data) {
        if (!StringUtils.hasText(data)) {
            return null;
        }
        JSONObject jsonData = JSONObject.parseObject(data);
        if (jsonData == null) {
            return null;
        }
        String cipherText = jsonData.getString("cipherText");
        if (!StringUtils.hasText(cipherText)) {
            return null;
        }
        return JSONObject.parseObject(cipherText);
    }

    public User update(User target, User source) {
        boolean update = false;
        if (!StringUtils.hasText(target.getCustomerId())) {
            target.setCustomerId(source.getCustomerId());
            update = true;
        }
        if (!StringUtils.hasText(target.getOpenId())) {
            target.setOpenId(source.getOpenId());
            update = true;
        }
        if (!StringUtils.hasText(target.getMobile())) {
            target.setMobile(source.getMobile());
            update = true;
        }
        if (!StringUtils.hasText(target.getBranchId())) {
            target.setBranchId(source.getBranchId());
            update = true;
        }
        if (!StringUtils.hasText(target.getIbknum())) {
            target.setIbknum(source.getIbknum());
            update = true;
        }
        if (update) {
            target.setUpdateTime(LocalDateTime.now());
        }
        return update ? userRepository.save(target) : target;

    }


    public Optional<User> findByOpenId(String openid) {
        return userRepository.findByOpenId(openid);
    }

    public Optional<User> findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }
    public Optional<User> findByUserId(int userId) {
        return userRepository.findById(userId);
    }
}
