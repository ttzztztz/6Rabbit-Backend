package com.rabbit.backend.Bean.Thread;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PostEditorForm {
    private String pid;

    private String quotepid;

    @NotBlank
    private String content;
}
