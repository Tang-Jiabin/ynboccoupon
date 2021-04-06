package com.zykj.yn.boc.coupon.common.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zykj.yn.boc.coupon.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 验证token的实现类
 *
 * @author tang
 */
@Slf4j
@Service("tokenManager")
public class TokenManager {

    private final RedisUtil redisUtil;

    private static final String SECRET = "R78{7(53!~3&>5}3}61^~LX,0m";
    private static final String ISSUER = "tangjiabin";
    private static final String KEY = "token";

    @Autowired
    public TokenManager(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    public TokenModel createToken(String userId) {

        String token = null;
        try {
            token = JWT.create()
                    .withIssuer(ISSUER)
                    .withJWTId(UUID.randomUUID().toString().toUpperCase())
                    .withClaim(KEY, userId)
                    .sign(Algorithm.HMAC256(SECRET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        TokenModel model = new TokenModel(userId, token);
        redisUtil.setStringSeconds("user:"+userId+":token", token, 60 * 60L * 2);

        return model;
    }

    public TokenModel getToken(String token) throws UnsupportedEncodingException, JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer(ISSUER)
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
        String userId = jwt.getClaim(KEY).asString();
        return new TokenModel(userId, jwt.getToken());
    }


    public boolean checkToken(String token) throws UnsupportedEncodingException, JWTVerificationException {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        TokenModel tokenModel = getToken(token);
        String dbToken = null;
        try {

            if (!ObjectUtils.isEmpty(tokenModel)) {
                dbToken = redisUtil.getString("user:"+tokenModel.getUserId()+":token");
                if (StringUtils.hasText(dbToken)) {
                    redisUtil.setStringSeconds("user:"+tokenModel.getUserId()+":token", token, 60 * 60L * 2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return token.equals(dbToken);
    }


}
