package com.rabbit.backend.Bean.Credits;

import lombok.Data;

import java.util.Date;

@Data
public class AttachPayLog implements PayLog {
    // for mybatis
    private String did;

    private String aid;
    private String uid;
    private Integer creditsType;
    private Integer credits;
    private Date createDate;
}
