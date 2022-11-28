package com.xfliu.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

@Slf4j
@Component
public class DingRobot {

    private String secret = "";

    private String webHook = "";

    public void sendDdRobot(String msg) {

        Long timestamp = System.currentTimeMillis();


        String stringToSign = timestamp + "\n" + secret;
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");


            String addSign = "&timestamp=" + timestamp + "&sign=" + sign;

            DingTalkClient dingTalkClient = new DefaultDingTalkClient(webHook + addSign);

            OapiRobotSendRequest robotSendRequest = new OapiRobotSendRequest();

            robotSendRequest.setMsgtype("text");


            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(msg);
            robotSendRequest.setText(text);


//        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
//        at.setAtMobiles(Arrays.asList("18722064905"));
//        at.setIsAtAll(false);
//        robotSendRequest.setAt(at);

            OapiRobotSendResponse response = dingTalkClient.execute(robotSendRequest);
        } catch (Exception e) {
            log.error("钉钉推送消息异常", e);
            e.printStackTrace();
        }


    }

}
