package com.rabbit.backend.Bean.Thread;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ThreadEditorForm {
    private String tid;

    @NotBlank
    private String fid;

    @NotBlank
    @Size(max = 255)
    private String subject;

    @NotBlank
    private String content;
}
