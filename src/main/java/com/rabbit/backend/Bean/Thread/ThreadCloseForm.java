package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
public class ThreadCloseForm {
    @NotBlank
    private String tid;

    @NotBlank
    @Range(min = 0, max = 1)
    private Integer close;
}
