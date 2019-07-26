package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.User.*;
import com.rabbit.backend.Security.JWTUtils;
import com.rabbit.backend.Security.PasswordUtils;
import com.rabbit.backend.Service.CreditsLogService;
import com.rabbit.backend.Service.UserService;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import com.rabbit.backend.Utilities.Response.FieldErrorResponse;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import com.rabbit.backend.Utilities.IPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.DigestUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService service;
    private CreditsLogService creditsLogService;

    @Autowired
    public UserController(UserService userService, CreditsLogService creditsLogService) {
        this.service = userService;
        this.creditsLogService = creditsLogService;
    }

    @GetMapping("/info/{uid}")
    public OtherUser info(@PathVariable("uid") String uid) {
        OtherUser user = service.selectOtherUserByUid(uid);
        if (user == null) {
            throw new NotFoundException(1, "user doesn't Exist");
        }
        return user;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }

        String IP = IPUtil.getIPAddress();
        if (!service.registerLimitCheck(IP)) {
            return GeneralResponse.generator(503, "Request too frequently.");
        }

        Boolean usernameExistenceCheck = service.exist("username", form.getUsername());
        Boolean emailExistenceCheck = service.exist("email", form.getEmail());
        if (usernameExistenceCheck) {
            return GeneralResponse.generator(400, "Username already exist.");
        }

        if (emailExistenceCheck) {
            return GeneralResponse.generator(400, "Email already exist.");
        }

        String uid = service.register(form.getUsername(), form.getPassword(), form.getEmail());
        service.registerLimitIncrement(IP);
        return GeneralResponse.generator(200, uid);
    }

    @PostMapping("/info/password")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> updatePassword(Authentication authentication,
                                              @Valid @RequestBody UpdatePasswordForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }

        String uid = (String) authentication.getPrincipal();

        User user = service.selectUser("uid", uid);
        if (user == null) {
            return GeneralResponse.generator(400, "Username or Password invalid.");
        }
        boolean checkResult = DigestUtils.md5DigestAsHex((form.getOldPassword() + user.getSalt()).getBytes()).equals(
                user.getPassword()
        );

        if (!checkResult) {
            return GeneralResponse.generator(400, "Username or Password invalid.");
        }
        service.updatePassword(uid, form.getNewPassword());
        return GeneralResponse.generator(200);
    }

    @PostMapping("/info/profile")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> updateProfile(Authentication authentication,
                                             @Valid @RequestBody UpdateProfileForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }
        String uid = (String) authentication.getPrincipal();

        service.updateProfile(uid, form);
        return GeneralResponse.generator(200);
    }

    @GetMapping("/info/my")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> myProfile(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        MyUser user = service.selectMyUser("uid", uid);

        return GeneralResponse.generator(200, user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }

        String IP = IPUtil.getIPAddress();
        if (!service.loginLimitCheck(IP)) {
            return GeneralResponse.generator(503, "Request too frequently.");
        }

        User user = service.selectUser("username", form.getUsername());
        if (user == null) {
            return GeneralResponse.generator(400, "Username or Password invalid.");
        }
        boolean loginResult = PasswordUtils.checkPassword(user.getPassword(), form.getPassword(), user.getSalt());

        if (loginResult) {
            String token = JWTUtils.sign(user.getUid(), user.getUsername(), user.getUsergroup().getIsAdmin());
            Map<String, Object> response = new HashMap<>();

            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("uid", user.getUid());
            return GeneralResponse.generator(200, response);
        } else {
            service.loginLimitIncrement(IP);
            return GeneralResponse.generator(400, "Username or Password invalid.");
        }
    }

    @GetMapping("/credits/log/{page}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> creditsLog(Authentication authentication, @PathVariable("page") Integer page) {
        String uid = (String) authentication.getPrincipal();
        CreditsLogListResponse response = new CreditsLogListResponse();

        response.setCount(creditsLogService.count(uid));
        response.setList(creditsLogService.list(uid, page));
        return GeneralResponse.generator(200, response);
    }
}
