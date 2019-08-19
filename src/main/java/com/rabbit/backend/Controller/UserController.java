package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Credits.CreditsLogListResponse;
import com.rabbit.backend.Bean.OAuth.OAuthUserInfo;
import com.rabbit.backend.Bean.User.*;
import com.rabbit.backend.Security.PasswordUtils;
import com.rabbit.backend.Service.*;
import com.rabbit.backend.Utilities.Exceptions.NotFoundException;
import com.rabbit.backend.Utilities.IPUtil;
import com.rabbit.backend.Utilities.Response.FieldErrorResponse;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.DigestUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;
    private CreditsLogService creditsLogService;
    private PayService payService;
    private MailService mailService;
    private ThreadService threadService;
    private PostService postService;
    private OAuthService oAuthService;

    @Autowired
    public UserController(UserService userService, CreditsLogService creditsLogService, PayService payService,
                          MailService mailService, ThreadService threadService, PostService postService, OAuthService oAuthService) {
        this.userService = userService;
        this.creditsLogService = creditsLogService;
        this.payService = payService;
        this.mailService = mailService;
        this.threadService = threadService;
        this.postService = postService;
        this.oAuthService = oAuthService;
    }

    @GetMapping("/thread/{uid}/{page}")
    public Map<String, Object> userThreadList(@PathVariable("uid") String uid, @PathVariable("page") Integer page) {
        UserThreadListResponse userThreadListResponse = new UserThreadListResponse();

        userThreadListResponse.setThreads(threadService.userThreads(uid));
        userThreadListResponse.setList(threadService.listByUser(uid, page));

        return GeneralResponse.generate(200, userThreadListResponse);
    }

    @GetMapping("/post/{uid}/{page}")
    public Map<String, Object> userPostList(@PathVariable("uid") String uid, @PathVariable("page") Integer page) {
        UserPostListResponse userPostListResponse = new UserPostListResponse();

        userPostListResponse.setPosts(postService.userPosts(uid));
        userPostListResponse.setList(postService.listByUser(uid, page));

        return GeneralResponse.generate(200, userPostListResponse);
    }

    @GetMapping("/info/{uid}")
    public Map<String, Object> info(@PathVariable("uid") String uid) {
        OtherUser user = userService.selectOtherUserByUid(uid);
        if (user == null) {
            throw new NotFoundException(1, "user doesn't Exist");
        }
        return GeneralResponse.generate(200, user);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        String IP = IPUtil.getIPAddress();
        if (!userService.registerLimitCheck(IP)) {
            return GeneralResponse.generate(503, "Request too frequently.");
        }

        Boolean usernameExistenceCheck = userService.exist("username", form.getUsername());
        Boolean emailExistenceCheck = userService.exist("email", form.getEmail());
        if (usernameExistenceCheck) {
            return GeneralResponse.generate(400, "Username already exist.");
        }

        if (emailExistenceCheck) {
            return GeneralResponse.generate(400, "Email already exist.");
        }

        String uid = userService.register(form.getUsername(), form.getPassword(), form.getEmail());
        userService.registerLimitIncrement(IP);
        mailService.sendMail(form.getEmail(), "感谢您注册酷兔网！", "感谢您注册酷兔网！请您在发表帖子时遵守法律法规，文明上网，理性发言！");

        if (form.getBindOAuthCode() != null && form.getBindOAuthPlatform() != null) {
            String platform = form.getBindOAuthPlatform();
            String code = form.getBindOAuthCode();

            OAuthUserInfo oAuthUserInfo = oAuthService.callback(platform, code);
            if (oAuthService.openidBindOtherUserExist(oAuthUserInfo.getOpenid(), platform)) {
                return GeneralResponse.generate(400, "Openid Already bind.");
            }

            oAuthService.bind(platform, code, uid, oAuthUserInfo.getOpenid());
        }

        return GeneralResponse.generate(200, uid);
    }

    @PutMapping("/info/password")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> updatePassword(Authentication authentication,
                                              @Valid @RequestBody UpdatePasswordForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        String uid = (String) authentication.getPrincipal();

        User user = userService.selectUser("uid", uid);
        if (user == null) {
            return GeneralResponse.generate(400, "Username or Password invalid.");
        }
        boolean checkResult = DigestUtils.md5DigestAsHex((form.getOldPassword() + user.getSalt()).getBytes()).equals(
                user.getPassword()
        );

        if (!checkResult) {
            return GeneralResponse.generate(400, "Username or Password invalid.");
        }
        userService.updatePassword(uid, form.getNewPassword());
        return GeneralResponse.generate(200);
    }

    @PutMapping("/info/my")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> updateProfile(Authentication authentication,
                                             @Valid @RequestBody UpdateProfileForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }
        String uid = (String) authentication.getPrincipal();

        userService.updateProfile(uid, form);
        return GeneralResponse.generate(200);
    }

    @GetMapping("/info/my")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> myProfile(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        MyUser user = userService.selectMyUser("uid", uid);

        return GeneralResponse.generate(200, user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        String IP = IPUtil.getIPAddress();
        if (!userService.loginLimitCheck(IP)) {
            return GeneralResponse.generate(503, "Request too frequently.");
        }

        User user = userService.selectUser("username", form.getUsername());
        if (user == null) {
            userService.loginLimitIncrement(IP);
            return GeneralResponse.generate(400, "Username or Password invalid.");
        }

        boolean loginResult = PasswordUtils.checkPassword(user.getPassword(), form.getPassword(), user.getSalt());

        if (loginResult) {
            return GeneralResponse.generate(200, userService.loginResponse(user));
        } else {
            userService.loginLimitIncrement(IP);
            return GeneralResponse.generate(400, "Username or Password invalid.");
        }
    }

    @GetMapping("/token")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> updateToken(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        User user = userService.selectUser("uid", uid);

        return GeneralResponse.generate(200, userService.loginResponse(user));
    }

    @GetMapping("/credits/log/{page}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> creditsLog(@PathVariable("page") Integer page, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        CreditsLogListResponse response = new CreditsLogListResponse();

        response.setCount(creditsLogService.count(uid));
        response.setList(creditsLogService.list(uid, page));

        return GeneralResponse.generate(200, response);
    }

    @GetMapping("/purchased/attach/{page}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> purchasedAttach(@PathVariable("page") Integer page, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        return GeneralResponse.generate(200, payService.attachPurchasedList(uid, page));
    }

    @GetMapping("/purchased/aggregate/{page}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> purchasedAggregateList(@PathVariable("page") Integer page, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        UserPurchasedListResponse userPurchasedListResponse = new UserPurchasedListResponse();

        userPurchasedListResponse.setList(userService.purchasedList(uid, page));
        userPurchasedListResponse.setCount(userService.purchasedListCount(uid));

        return GeneralResponse.generate(200, userPurchasedListResponse);
    }
}
