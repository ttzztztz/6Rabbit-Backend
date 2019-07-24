package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ThreadEditorForm {
    // for mybatis
    private String tid;
    private String uid;
    private String firstpid;

    @NotBlank
    private String fid;

    @NotBlank
    @Size(max = 255)
    private String subject;

    @NotBlank
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.RELAXED)
    private String message;
}
