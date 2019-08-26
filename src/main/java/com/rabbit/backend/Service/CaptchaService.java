package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Verify.VerifyResponse;
import com.rabbit.backend.Utilities.Exceptions.CaptchaException;
import com.rabbit.backend.Utilities.IPUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaptchaService {
    @Value("${rabbit.vaptcha.ID}")
    private String VID;

    @Value("${rabbit.vaptcha.SECRET}")
    private String SECRET;

    public void verifyToken(String token) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("id", VID);
        requestMap.put("secretkey", SECRET);
        requestMap.put("scene", "");
        requestMap.put("token", token);
        requestMap.put("ip", IPUtil.getIPAddress());
        VerifyResponse verifyResponse = restTemplate.postForObject("https://api.vaptcha.com/v2/validate",
                requestMap, VerifyResponse.class);

        if (verifyResponse == null || verifyResponse.getSuccess() != 1) {
            throw new CaptchaException(403, "Captcha invalid!");
        }
    }
}
