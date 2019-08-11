package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Utilities.SafeHtml;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
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
