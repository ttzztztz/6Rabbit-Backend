package com.rabbit.backend.Bean.User;

import com.rabbit.backend.Bean.Group.Group;
import lombok.Data;

@Data
public class UserLoginResponse {
    private String token;
    private String uid;
    private String username;
    private Group usergroup;
    private Integer credits;
    private Integer golds;
    private Integer rmbs;
}
