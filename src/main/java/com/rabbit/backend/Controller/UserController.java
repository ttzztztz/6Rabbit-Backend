package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.User.LoginForm;
import com.rabbit.backend.Bean.User.OtherUser;
import com.rabbit.backend.Bean.User.RegisterForm;
import com.rabbit.backend.Bean.User.User;
import com.rabbit.backend.Security.JWTUtils;
import com.rabbit.backend.Service.UserService;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import com.rabbit.backend.Utilities.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
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
    @PermitAll()
    public OtherUser info(@PathVariable("uid") String uid) {
        OtherUser user = service.selectOtherUserByUid(uid);
        if (user == null) {
            throw new NotFoundException(1, "user doesn't Exist");
        }
        return user;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    @PermitAll()
    public Map<String, Object> register(@RequestBody RegisterForm form) {
        Boolean existenceCheck = service.usernameExist(form.getUsername());
        if (existenceCheck) {
            return ResponseGenerator.generator(-1, "Username already existence.");
        }

        service.register(form.getUsername(), form.getPassword(), form.getEmail());
        return ResponseGenerator.generator(1);
    }

    @PostMapping("/info/password")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> updatePassword(Authentication authentication, @RequestBody RegisterForm form) {
        String uid = (String) authentication.getPrincipal();

        return ResponseGenerator.generator(1);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @PermitAll()
    public Map<String, Object> login(@RequestBody LoginForm loginForm) {
        User user = service.selectUser("username", loginForm.getUsername());
        if (user == null) {
            return ResponseGenerator.generator(-1, "Username or Password invalid.");
        }

        boolean loginResult = DigestUtils.md5DigestAsHex((loginForm.getPassword() + user.getSalt()).getBytes()).equals(
                user.getPassword()
        );

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
