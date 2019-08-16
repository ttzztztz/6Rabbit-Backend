package com.rabbit.backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SEOService {
    @Value("${rabbit.frontend.site}")
    private String frontendURL;

    @Value("${rabbit.seo.baidu.token}")
    private String baiduToken;

    @Async
    public void pushToBaidu(String tid) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String postURL = "http://data.zz.baidu.com/urls?site=" + frontendURL + "&token=" + baiduToken;
            restTemplate.postForObject(postURL, frontendURL + "/thread/" + tid + "/1", String.class);
        } catch (Exception e) {
            // do nothing...
        }
    }
}
