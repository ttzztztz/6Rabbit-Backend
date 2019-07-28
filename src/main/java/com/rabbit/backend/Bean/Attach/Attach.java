package com.rabbit.backend.Bean.Attach;

import com.rabbit.backend.Bean.User.OtherUser;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public class Attach {
    private String aid;
    private String tid;
    private OtherUser user;
    private Integer fileSize;
    private Integer downloads;
    private String fileName;
    private String originalName;
    private Integer creditsType;
    private Integer credits;
    private Date createDate;
}
