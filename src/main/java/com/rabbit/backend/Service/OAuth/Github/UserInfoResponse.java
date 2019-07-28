package com.rabbit.backend.Service.OAuth.Github;

import lombok.Data;

@Data
class UserInfoResponse {
    private String id;
    private String login;
    private String avatar_url;
}
