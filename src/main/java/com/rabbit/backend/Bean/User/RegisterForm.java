package com.rabbit.backend.Bean.User;

import lombok.Data;

@Data
public class RegisterForm {
    private String username;
    private String password;
    private String email;
}
