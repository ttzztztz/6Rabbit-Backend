package com.rabbit.backend.Bean.OAuth;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
public abstract class OAuthWebsite {
    private String APP_ID;
    private String APP_SECRET;
    private String CONNECT_URL;
    private String TOKEN_URL;
    private String USER_INFO_URL;

    public abstract String buildLoginURL(String redirect);

    public abstract String getCode(HttpServletRequest request);

    public abstract String getAccessToken(String code);

    public abstract OAuthUserInfo getUserInfo(String access_token);
}
