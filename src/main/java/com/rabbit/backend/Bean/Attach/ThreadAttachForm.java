package com.rabbit.backend.Bean.Attach;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;


@Data
public class ThreadAttachForm {
    @NotBlank
    private String aid;

    @Range(min = 0, max = 3)
    private Integer creditsType;

    @Range(min = 0, max = 1000)
    private Integer credits;
}
