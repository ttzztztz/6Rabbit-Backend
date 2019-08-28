package com.rabbit.backend.Bean.Credits;

import com.rabbit.backend.Bean.User.OtherUser;
import lombok.Data;

import java.util.Date;

@Data
public class CreditsLog {
    private String cid;
    private OtherUser user;
    private Integer status;
    private String type;
    private String description;
    private Integer creditsType;
    private Integer credits;
    private Date createDate;
}
