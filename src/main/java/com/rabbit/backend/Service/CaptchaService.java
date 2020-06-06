package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Verify.VerifyResponse;
import com.rabbit.backend.Utilities.Exceptions.CaptchaException;
import com.rabbit.backend.Utilities.IPUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {
    @Value("${rabbit.vaptcha.ID}")
    private String VID;

    @Value("${rabbit.vaptcha.SECRET}")
    private String SECRET;

    public void verifyToken(String token) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("id", VID);
        requestMap.add("secretkey", SECRET);
        requestMap.add("scene", "");
        requestMap.add("token", token);
        requestMap.add("ip", IPUtil.getIPAddress());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestMap, headers);

        VerifyResponse verifyResponse = restTemplate.postForObject("http://0.vaptcha.com/verify",
                formEntity, VerifyResponse.class);

        if (verifyResponse == null || verifyResponse.getSuccess() != 1) {
            String errorMessage = verifyResponse == null ? "Invalid code" : verifyResponse.getMsg();

            throw new CaptchaException(403, errorMessage == null ? "Invalid code" : errorMessage);
        }
    }
}
