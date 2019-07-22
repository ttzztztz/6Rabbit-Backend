package com.rabbit.backend.Bean;

import org.springframework.stereotype.Component;
import lombok.Data;

@Component("user")
@Data
public class User {
    private int uid;
    private String username;
    private String password;
    private String salt;
    private int gender;
    private String realname;
    private String email;
    private String mobile;
    private String qq;
    private String wechat;

}
