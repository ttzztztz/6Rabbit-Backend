package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
public class ThreadTopForm {
    @NotBlank
    private String tid;

    @NotBlank
    @Range(min = 0, max = 2)
    private Integer top;
}
