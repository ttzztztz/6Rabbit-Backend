package com.rabbit.backend.Bean.User;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class UpdateProfileForm {
    @Size(max = 64, message = "Realname too long.")
    public String realname = "";

    @Range(min = 0, max = 2, message = "Gender invalid.")
    public Integer gender = 0;

    @Email(message = "Email format invalid.")
    public String email;

    @Size(max = 32, message = "QQ too long.")
    public String qq = "";

    @Size(max = 32, message = "Mobile too long.")
    public String mobile = "";

    @Size(max = 64, message = "Wechat too long.")
    public String wechat = "";

    @Size(max = 72, message = "Signature too long.")
    public String signature = "";
}
