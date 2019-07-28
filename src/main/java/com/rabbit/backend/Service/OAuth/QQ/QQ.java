package com.rabbit.backend.Service.OAuth.QQ;

import com.rabbit.backend.Bean.OAuth.OAuthUserInfo;
import com.rabbit.backend.Bean.OAuth.OAuthWebsite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

// todo: not finished yet !
@Configuration("OAuth_QQ")
public class QQ extends OAuthWebsite {
    @Value("${rabbit.oauth.qq.APP_ID}")
    private String APP_ID;

    @Value("${rabbit.oauth.qq.APP_SECRET}")
    private String APP_SECRET;

    private String CONNECT_URL = "https://graph.qq.com/oauth2.0/authorize";
    private String TOKEN_URL = "https://graph.qq.com/oauth2.0/token";
    private String ME_URL = "https://graph.qq.com/oauth2.0/me";
    private String USER_INFO_URL = "https://graph.qq.com/user/get_user_info";

    public String buildLoginURL(String redirect) {
        Random rand = new Random(System.currentTimeMillis());
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

        multiValueMap.add("response_type", "code");
        multiValueMap.add("client_id", APP_ID);
        multiValueMap.add("redirect_uri", redirect);
        multiValueMap.add("state", String.valueOf(rand.nextInt(1000000)));
        multiValueMap.add("scope", "get_user_info,add_t,add_pic_t,add_share");

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(
                this.CONNECT_URL).queryParams(multiValueMap).build();

        return uriComponents.encode().toUri().toString();
    }

    public String getCode(HttpServletRequest request) {
        return request.getParameter("code");
    }

    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        MultiValueMap params = new LinkedMultiValueMap();
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(params, headers);

        AccessTokenResponse accessTokenResponse = restTemplate.postForObject(TOKEN_URL + "?" +
                "client_id=" + APP_ID +
                "&client_secret=" + APP_SECRET +
                "&code=" + code, requestEntity, AccessTokenResponse.class);

        if (accessTokenResponse == null) {
            return null;
        } else {
            return accessTokenResponse.getAccess_token();
        }
    }

    public OAuthUserInfo getUserInfo(String access_token) {
        OAuthUserInfo result = new OAuthUserInfo();

        RestTemplate restTemplate = new RestTemplate();
        UserInfoResponse userInfoResponse = restTemplate.getForObject(USER_INFO_URL + "?" +
                "access_token=" + access_token, UserInfoResponse.class);

        if (userInfoResponse == null) {
            return null;
        }

        result.setAvatarURL(userInfoResponse.getAvatar_url());
        result.setOpenid(userInfoResponse.getId());
        result.setUsername(userInfoResponse.getLogin());
        return result;
    }
}
