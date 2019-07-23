package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.User.*;
import com.rabbit.backend.Security.JWTUtils;
import com.rabbit.backend.Security.PasswordUtils;
import com.rabbit.backend.Service.UserService;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import com.rabbit.backend.Utilities.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService userService) {
        this.service = userService;
    }

    @GetMapping("/info/{uid}")
    @ResponseStatus(HttpStatus.OK)
    public OtherUser info(Authentication authentication, @PathVariable("uid") String uid) {
        OtherUser user = service.selectOtherUserByUid(uid);
        if (user == null) {
            throw new NotFoundException(1, "user doesn't Exist");
        }
        return user;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> register(@Valid @RequestBody RegisterForm form) {
        Boolean usernameExistenceCheck = service.exist("username", form.getUsername());
        Boolean emailExistenceCheck = service.exist("email", form.getEmail());
        if (usernameExistenceCheck || emailExistenceCheck) {
            return ResponseGenerator.generator(-1, "Username already existence.");
        }

        service.register(form.getUsername(), form.getPassword(), form.getEmail());
        return ResponseGenerator.generator(1);
    }

    @PostMapping("/info/password")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> updatePassword(Authentication authentication,
                                              @Valid @RequestBody UpdatePasswordForm form
    ) {
        String uid = (String) authentication.getPrincipal();

        User user = service.selectUser("uid", uid);
        if (user == null) {
            return ResponseGenerator.generator(-1, "Username or Password invalid.");
        }
        boolean checkResult = DigestUtils.md5DigestAsHex((form.getOldPassword() + user.getSalt()).getBytes()).equals(
                user.getPassword()
        );

        if (!checkResult) {
            return ResponseGenerator.generator(-1, "Username or Password invalid.");
        }
        service.updatePassword(uid, form.getNewPassword());
        return ResponseGenerator.generator(1);
    }

    @PostMapping("/info/profile")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> updateProfile(Authentication authentication,
                                             @Valid @RequestBody UpdateProfileForm form) {
        String uid = (String) authentication.getPrincipal();

        final String[] keys = {"realname", "gender", "email", "qq", "mobile", "wechat", "signature"};
        String[] values = {form.getRealname(), form.getGender(), form.getQq(), form.getMobile(), form.getWechat(), form.getSignature()};

        service.updateProfile(uid, keys, values);
        return ResponseGenerator.generator(1);
    }

    @GetMapping("/info/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> myProfile(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        MyUser user = service.selectMyUser("uid", uid);

        return ResponseGenerator.generator(1, user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> login(@RequestBody LoginForm form) {
        User user = service.selectUser("username", form.getUsername());
        if (user == null) {
            return ResponseGenerator.generator(-1, "Username or Password invalid.");
        }
        boolean loginResult = PasswordUtils.checkPassword(user.getPassword(), form.getPassword(), user.getSalt());

        if (loginResult) {
            String token = JWTUtils.sign(user.getUid().toString(), user.getUsername(), user.getUsergroup().getIsAdmin());
            Map<String, Object> response = new HashMap<>();

            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("uid", user.getUid());
            return ResponseGenerator.generator(1, response);
        } else {
            return ResponseGenerator.generator(-1, "Username or Password invalid.");
        }
    }
}
