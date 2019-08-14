package com.rabbit.backend.Bean.OAuth;

import lombok.Data;

@Data
public abstract class OAuthWebsite {
    private String APP_ID;
    private String APP_SECRET;
    private String CONNECT_URL;
    private String TOKEN_URL;
    private String USER_INFO_URL;

    public abstract String buildLoginURL();

    public abstract String getAccessToken(String code);

    public abstract OAuthUserInfo getUserInfo(String access_token);
}
