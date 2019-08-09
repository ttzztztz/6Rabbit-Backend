package com.rabbit.backend.Bean.Thread;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PostEditorForm {
    // for mybatis
    private String pid;
    private String uid;
    private String tid;

    private String quotepid = "0";

    @NotBlank(message = "Message mustn't be null")
    @Size(min = 5, message = "Message must have more than 5 characters")
    private String message;

    public String getMessage() {
        return com.rabbit.backend.Utilities.SafeHtml.sanitize(this.message);
    }
}
