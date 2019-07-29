package com.rabbit.backend.Service.OAuth.QQ;

import com.rabbit.backend.Bean.OAuth.OAuthUserInfo;
import com.rabbit.backend.Bean.OAuth.OAuthWebsite;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    private Map<String, String> getParam(String string) {
        Map<String, String> map = new HashMap<>();
        String[] kvArray = string.split("&");
        for (String s : kvArray) {
            String[] kv = s.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap params = new LinkedMultiValueMap();
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(params, new HttpHeaders());

        String accessTokenResponse = restTemplate.postForObject(TOKEN_URL + "?" +
                "client_id=" + APP_ID +
                "&client_secret=" + APP_SECRET +
                "&code=" + code, requestEntity, String.class);

        if (accessTokenResponse == null || !accessTokenResponse.contains("access_token")) {
            return null;
        } else {
            Map<String, String> accessTokenResponseMap = getParam(accessTokenResponse);
            return accessTokenResponseMap.get("access_token");
        }
    }

    public String getOpenId(String access_token) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String openIdResponse = restTemplate.getForObject(ME_URL + "?" +
                "access_token=" + access_token, String.class);

        if (openIdResponse == null) {
            return null;
        }

        String structuredResponse = openIdResponse
                .substring(openIdResponse.indexOf("(") + 1)
                .substring(0, openIdResponse.indexOf(")"));

        JSONParser jsonParser = new JSONParser(structuredResponse);
        return (String) jsonParser.parseObject().get("openid");
    }

    public OAuthUserInfo getUserInfo(String access_token) {
        if (access_token == null) {
            return null;
        }

        OAuthUserInfo result = new OAuthUserInfo();
        try {
            String openid = getOpenId(access_token);

            RestTemplate restTemplate = new RestTemplate();
            UserInfoResponse userInfoResponse = restTemplate.getForObject(USER_INFO_URL + "?" +
                    "access_token=" + access_token +
                    "&openid=" + openid +
                    "&&oauth_consumer_key=" + APP_ID, UserInfoResponse.class);

            if (userInfoResponse == null) {
                return null;
            }

            result.setAvatarURL(userInfoResponse.getFigureurl_qq_1() != null
                    ? userInfoResponse.getFigureurl_qq_1()
                    : userInfoResponse.getFigureurl_qq_2());

            result.setOpenid(openid);
            result.setUsername(userInfoResponse.getNickname());
        } catch (ParseException ex) {
            return null;
        }

        return result;
    }
}
