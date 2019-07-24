package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.NotBlank;

@Data
public class PostEditorForm {
    // for mybatis
    private String pid;
    private String uid;
    private String tid;

    private Integer quotepid = 0;
    @NotBlank
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.RELAXED)
    private String message;
}
