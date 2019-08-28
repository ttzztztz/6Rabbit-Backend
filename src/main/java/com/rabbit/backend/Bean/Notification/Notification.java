package com.rabbit.backend.Bean.Notification;

import com.rabbit.backend.Bean.User.OtherUser;
import com.rabbit.backend.Utilities.SafeHtml;
import lombok.Data;

import java.util.Date;

@Data
public class Notification {
    private String nid;
    private OtherUser fromUser;
    private OtherUser toUser;
    private String content;
    private String link;
    private Boolean isRead;
    private Date createDate;

    public String getContent() {
        return SafeHtml.sanitize(this.content);
    }
}
