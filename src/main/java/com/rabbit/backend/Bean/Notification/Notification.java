package com.rabbit.backend.Bean.Notification;

import com.rabbit.backend.Bean.User.OtherUser;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("notification")
@Data
public class Notification {
    private Integer id;
    private OtherUser fromUser;
    private OtherUser toUser;
    private String content;
    private String link;
    private Boolean isRead;
    private Date createDate;
}
