package com.rabbit.backend.Bean.User;

import lombok.Data;

@Data
public class PasswordForm {
    private String oldPassword;
    private String newPassword;
}
