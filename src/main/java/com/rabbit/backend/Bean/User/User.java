package com.rabbit.backend.Bean.User;

import com.rabbit.backend.Bean.Group.Group;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("user")
@Data
public class User {
    private String uid;
    private Group usergroup;
    private String username;
    private String password;
    private String salt;
    private Integer gender;
    private String realname;
    private String email;
    private String mobile;
    private String qq;
    private String wechat;
    private Integer credits;
    private Integer golds;
    private Integer rmbs;
    private Date createDate;
    private Date loginDate;
}