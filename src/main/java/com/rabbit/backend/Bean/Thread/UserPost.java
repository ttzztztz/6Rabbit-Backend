package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Utilities.SafeHtml;
import lombok.Data;

import java.util.Date;

@Data
public class UserPost {
    private String pid;
    private ThreadListItem thread;
    private Boolean isFirst;
    private String message;
    private Date createDate;

    public String getMessage() {
        return SafeHtml.sanitize(this.message);
    }
}
