package com.rabbit.backend.Bean.User;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("creditlog")
@Data
public class CreditsLog {
    private String cid;
    private OtherUser user;
    private Integer status;
    private Integer type;
    private String description;
    private Integer creditsType;
    private Integer credits;
    private Date createDate;
}
