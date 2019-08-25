package com.rabbit.backend.Bean.Credits;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;

@Data
public class DepositSubmitForm {
    @Range(min = 1000, max = 1000000)
    private Integer credits;

    @Size(max = 72)
    private String description;
}
