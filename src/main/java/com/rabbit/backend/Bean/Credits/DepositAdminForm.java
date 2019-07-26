package com.rabbit.backend.Bean.Credits;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
public class DepositAdminForm {
    @NotBlank
    private String cid;

    @Range(min = -1, max = 1)
    private Integer status;
}
