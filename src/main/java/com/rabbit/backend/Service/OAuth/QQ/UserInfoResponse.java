package com.rabbit.backend.Service.OAuth.QQ;

import lombok.Data;

@Data
class UserInfoResponse {
    private String id;
    private String login;
    private String avatar_url;
}
