package com.zykj.yn.boc.coupon.utils;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * 发短信
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-29
 */
@Slf4j
public class RlySmsUtil {

    private static final String SMS_STATUS = "statusCode";
    private static final String SMS_CODE = "000000";

    private static final CCPRestSmsSDK SDK = new CCPRestSmsSDK();

    static {
        String serverIp = "app.cloopen.com";
        String serverPort = "8883";
        String appId = "8aaf07086b8862cb016b96baab4709b2";
        String accountsId = "8a216da86b8863a1016b9248fa7006fa";
        String accountToken = "ada5cc7b7ccc4d79ae3bdcbd58d2f183";
        SDK.init(serverIp, serverPort);
        SDK.setAccount(accountsId, accountToken);
        SDK.setAppId(appId);
        SDK.setBodyType(BodyType.Type_JSON);
    }

    public static boolean send(String mobile, String[] content) {
        String templateId = "443273";
        HashMap<String, Object> result = SDK.sendTemplateSMS(mobile, templateId, content);
        String status = (String) result.get(SMS_STATUS);
        log.info("发送短信：\t{}\t{}\t{}", mobile, content[0], status);
        if (SMS_CODE.equals(status)) {
            return true;
        }
        return false;
    }

}
