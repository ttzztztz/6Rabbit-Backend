package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Bean.User.OtherUser;
import com.rabbit.backend.Utilities.SafeHtml;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public class Post {
    private String pid;
    private OtherUser user;
    private String tid;
    private QuotePost quote;
    private Boolean isFirst;
    private String message;
    private Date createDate;

    public String getMessage() {
        return SafeHtml.sanitize(this.message);
    }
}
