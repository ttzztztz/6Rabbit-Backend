package com.rabbit.backend.Bean.User;

import com.rabbit.backend.Bean.Group.Group;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component("myuser")
@Data
public class MyUser {
    private Integer uid;
    private Group usergroup;
    private String username;
    private Integer gender;
    private String realname;
    private String email;
    private String mobile;
    private String qq;
    private String wechat;
    private Integer credits;
    private Integer golds;
    private Integer rmbs;
}