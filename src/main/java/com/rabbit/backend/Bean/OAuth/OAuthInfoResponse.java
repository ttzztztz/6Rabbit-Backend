package com.rabbit.backend.Bean.OAuth;

import lombok.Data;

@Data
public class OAuthInfoResponse {
    private OAuthUserInfo userInfo;
    private Boolean active;
}
