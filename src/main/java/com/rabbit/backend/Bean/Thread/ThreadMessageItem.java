package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Utilities.SafeHtml;
import lombok.Data;

@Data
public class ThreadMessageItem {
    private String tid;
    private String message;

    public String getMessage() {
        return SafeHtml.sanitize(this.message);
    }
}
