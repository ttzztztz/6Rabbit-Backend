package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Bean.User.OtherUser;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component("post")
public class Post {
    private String pid;
    private OtherUser user;
    private String tid;
    private Post quote;
    private Boolean isFirst;
    private String message;
    private Date createDate;
}
