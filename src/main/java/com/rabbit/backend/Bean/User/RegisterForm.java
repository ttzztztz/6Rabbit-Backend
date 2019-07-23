package com.rabbit.backend.Bean.User;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterForm {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Email(message = "Email format invalid.")
    private String email;
}
