package com.rabbit.backend.Bean.OAuth;

import lombok.Data;

@Data
public class OAuthUserInfo {
    private String openid;
    private String username;
    private String avatarURL;
}
