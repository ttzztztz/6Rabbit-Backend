package com.rabbit.backend.Bean.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminUpdateProfileForm extends UpdateProfileForm {
    private String newPassword = "";

    private Integer credits = 0;
    private Integer golds = 0;
    private Integer rmbs = 0;
}
