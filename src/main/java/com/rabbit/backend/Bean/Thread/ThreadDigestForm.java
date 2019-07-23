package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
public class ThreadDigestForm {
    @NotBlank
    private String tid;

    @NotBlank
    @Range(min = 0, max = 3)
    private Integer digest;
}
