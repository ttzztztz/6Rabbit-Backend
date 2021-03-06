package com.rabbit.backend.Service.OAuth.Github;

import com.rabbit.backend.Bean.OAuth.OAuthUserInfo;
import com.rabbit.backend.Bean.OAuth.OAuthWebsite;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration("OAuth_Github")
public class Github extends OAuthWebsite {
    @Value("${rabbit.oauth.github.APP_ID}")
    private String APP_ID;

    @Value("${rabbit.oauth.github.APP_SECRET}")
    private String APP_SECRET;

    private String CONNECT_URL = "https://github.com/login/oauth/authorize";
    private String TOKEN_URL = "https://github.com/login/oauth/access_token";
    private String USER_INFO_URL = "https://api.github.com/user";

    public String buildLoginURL() {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

        multiValueMap.add("response_type", "code");
        multiValueMap.add("client_id", APP_ID);

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(
                this.CONNECT_URL).queryParams(multiValueMap).build();

        return uriComponents.encode().toUri().toString();
    }

    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(new LinkedMultiValueMap(), headers);

        AccessTokenResponse accessTokenResponse = restTemplate.postForObject(TOKEN_URL + "?" +
                "client_id=" + APP_ID +
                "&client_secret=" + APP_SECRET +
                "&code=" + code, requestEntity, AccessTokenResponse.class);

        if (accessTokenResponse == null || accessTokenResponse.getAccess_token() == null) {
            throw new NotFoundException(400, "Invalid code.");
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
