package com.zykj.yn.boc.coupon.utils;

import com.bocnet.common.security.PKCS7Tool;
import com.zykj.yn.boc.coupon.common.CouponConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * 签名
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-25
 */
@Component
public class Pkcs7Util {

    private final CouponConfig couponConfig;

    private static PKCS7Tool signTool = null;
    private static PKCS7Tool verifyTool = null;

    public Pkcs7Util(CouponConfig couponConfig) {
        this.couponConfig = couponConfig;
    }

    public String sign(byte[] data) {
        try {
            return getSignTool().sign(data);
        } catch (Exception e) {
            return "";
        }
    }

    public String sign(String data) {
        try {
            byte[] plainTextByte = data.getBytes(StandardCharsets.UTF_8);
            return getSignTool().sign(plainTextByte);
        } catch (Exception e) {
            return "";
        }
    }

    private synchronized PKCS7Tool getSignTool() throws GeneralSecurityException, IOException {
        if (signTool == null) {
            String keyStorePath = couponConfig.getKeyStorePath();
            String keystorePwd = couponConfig.getKeystorePwd();
            InputStream inputStream = this.getClass().getResourceAsStream(keyStorePath);
            signTool = PKCS7Tool.getSigner(inputStream, KeyStore.getDefaultType(), keystorePwd, keystorePwd);
        }
        return signTool;
    }


    public boolean verifySign(String signData, String plainText) {
        try {
            getVerifyTool().verify(signData, plainText.getBytes(StandardCharsets.UTF_8), null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private synchronized PKCS7Tool getVerifyTool() throws GeneralSecurityException, IOException {
        if (verifyTool == null) {
            String certificatePath = couponConfig.getCertificatePath();
            InputStream resourceAsStream = this.getClass().getResourceAsStream(certificatePath);
            verifyTool = PKCS7Tool.getVerifier(resourceAsStream, "DER");
        }
        return verifyTool;
    }
}
