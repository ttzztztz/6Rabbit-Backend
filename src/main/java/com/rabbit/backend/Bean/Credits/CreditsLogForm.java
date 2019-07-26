package com.rabbit.backend.Bean.Credits;

import lombok.Data;

@Data
public class CreditsLogForm {
    // for mybatis
    private String cid;

    private String uid;
    private String status;
    private String type;
    private String description;
    private Integer creditsType;
    private Integer credits;
}
