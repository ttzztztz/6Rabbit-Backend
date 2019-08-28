package com.rabbit.backend.Bean.Group;

import lombok.Data;

@Data
public class Group {
    private String gid;
    private String name;
    private Boolean isAdmin;
    private Boolean canLogin;
    private Boolean canPost;
}
