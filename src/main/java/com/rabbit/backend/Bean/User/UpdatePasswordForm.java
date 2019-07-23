package com.rabbit.backend.Bean.User;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdatePasswordForm {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
