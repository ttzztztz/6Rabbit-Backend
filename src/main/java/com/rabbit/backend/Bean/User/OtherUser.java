package com.rabbit.backend.Bean.User;

import com.rabbit.backend.Bean.Group.Group;
import lombok.Data;

@Data
public class OtherUser {
    private String uid;
    private String username;
    private Group usergroup;
    private String signature;
}