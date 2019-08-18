package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Bean.User.OtherUser;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public abstract class Thread {
    private String tid;
    private OtherUser user;
    private String subject;
    private Integer posts;
    private Boolean isTop;
    private Boolean isClosed;
    private Integer diamond;
    private OtherUser lastUser;
    private String firstpid;
    private String lastpid;
    private Date createDate;
    private Date replyDate;
}
