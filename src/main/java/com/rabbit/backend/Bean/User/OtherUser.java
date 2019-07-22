package com.rabbit.backend.Bean.User;

import com.rabbit.backend.Bean.Group.Group;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component("otheruser")
@Data
public class OtherUser {
    private int uid;
    private String username;
    private Group usergroup;
}