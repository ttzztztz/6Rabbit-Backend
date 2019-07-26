package com.rabbit.backend.Bean.Thread;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PostEditorForm {
    // for mybatis
    private String pid;
    private String uid;
    private String tid;

    private Integer quotepid = 0;
    @NotBlank
    private String message;

    public String getMessage() {
        return com.rabbit.backend.Utilities.SafeHtml.sanitize(this.message);
    }
}
