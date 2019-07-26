package com.rabbit.backend.Bean.Credits;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class DepositSubmitForm {
    @Range(min = 100, max = 1000000)
    private Integer credits;
}
