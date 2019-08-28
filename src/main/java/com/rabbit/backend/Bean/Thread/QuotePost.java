package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Bean.User.OtherUser;
import com.rabbit.backend.Utilities.SafeHtml;
import lombok.Data;

import java.util.Date;

@Data
public class QuotePost {
    private String pid;
    private OtherUser user;
    private String message;
    private Date createDate;

    public String getMessage() {
        return SafeHtml.sanitize(this.message);
    }
}
