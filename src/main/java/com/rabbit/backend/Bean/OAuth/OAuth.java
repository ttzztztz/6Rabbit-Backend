package com.rabbit.backend.Bean.OAuth;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class OAuth {
    private String oid;
    private String uid;
    private String platform;
    private String openid;
}
