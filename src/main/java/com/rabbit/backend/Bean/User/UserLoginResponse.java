package com.rabbit.backend.Bean.User;

import lombok.Data;

@Data
public class UserLoginResponse {
    private String token;
    private String uid;
    private String username;
    private Boolean isAdmin;
}
