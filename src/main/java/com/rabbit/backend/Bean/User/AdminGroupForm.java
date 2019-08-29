package com.rabbit.backend.Bean.User;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdminGroupForm {
    @NotBlank
    private String uid;

    @NotBlank
    private String gid;
}
