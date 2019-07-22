package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.User;
import com.rabbit.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/info/{uid}", method = RequestMethod.GET)
    public User userInfo(@PathVariable("uid") String uid){
        return userService.selectUserByUid(uid);
    }
}
