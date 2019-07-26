package com.rabbit.backend.Bean.Credits;

import lombok.Data;

import java.util.Date;

@Data
public class ThreadPayLog implements PayLog {
    // for mybatis
    private String bid;

    private String tid;
    private String uid;
    private Integer creditsType;
    private Integer credits;
    private Date createDate;
}
