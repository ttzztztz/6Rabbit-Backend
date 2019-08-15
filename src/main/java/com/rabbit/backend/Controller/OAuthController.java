package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.OAuth.OAuth;
import com.rabbit.backend.Bean.OAuth.OAuthInfoResponse;
import com.rabbit.backend.Bean.OAuth.OAuthUserInfo;
import com.rabbit.backend.Bean.User.User;
import com.rabbit.backend.Service.FileService;
import com.rabbit.backend.Service.OAuthService;
import com.rabbit.backend.Service.UserService;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private OAuthService oAuthService;
    private UserService userService;
    private FileService fileService;

    @Autowired
    public OAuthController(OAuthService oAuthService, UserService userService, FileService fileService) {
        this.oAuthService = oAuthService;
        this.userService = userService;
        this.fileService = fileService;
    }

    @DeleteMapping("/{platform}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> delete(@PathVariable("platform") String platform, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();

        if (!oAuthService.userBindOtherPlatformExist(platform, uid)) {
            return GeneralResponse.generate(404, "Not found.");
        }

        oAuthService.deleteByUser(platform, uid);
        return GeneralResponse.generate(200);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> list(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        return GeneralResponse.generate(200, oAuthService.list(uid));
    }

    @GetMapping("/{platform}")
    public void getLoginURL(@PathVariable("platform") String platform, HttpServletResponse response) {
        response.setStatus(301);
        response.setHeader("Refresh", "0;URL=" + oAuthService.getURL(platform));
    }

    @GetMapping("/login/{platform}/{code}")
    public Map<String, Object> login(@PathVariable("platform") String platform, @PathVariable("code") String code) {
        OAuthUserInfo oAuthUserInfo = oAuthService.callback(platform, code);
        if (oAuthUserInfo == null) {
            return GeneralResponse.generate(500, "Connection to OAuth Server error.");
        }

        OAuth oAuth = oAuthService.findByOpenid(oAuthUserInfo.getOpenid(), platform);
        if (oAuth == null) {
            return GeneralResponse.generate(404, "Platform doesn't exist.");
        } else {
            User user = userService.selectUser("uid", oAuth.getUid());
            String avatarPath = fileService.avatarPath(oAuth.getUid());
            String avatarRemoteAddress = oAuthUserInfo.getAvatarURL();
            try {
                fileService.downloadRemoteFile(avatarPath, avatarRemoteAddress);
                oAuthService.deleteTokenFromCache(platform, code);
                return GeneralResponse.generate(200, userService.loginResponse(user));
            } catch (IOException exception) {
                return GeneralResponse.generate(500, exception.getMessage());
            }
        }
    }

    @GetMapping("/bind/{platform}/{code}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> bind(@PathVariable("platform") String platform, @PathVariable("code") String code,
                                    Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        OAuthUserInfo oAuthUserInfo = oAuthService.callback(platform, code);

        if (oAuthService.userBindOtherPlatformExist(platform, uid)) {
            return GeneralResponse.generate(400, "User Already bind.");
        }

        if (oAuthService.openidBindOtherUserExist(oAuthUserInfo.getOpenid(), platform)) {
            return GeneralResponse.generate(400, "Openid Already bind.");
        }

        oAuthService.bind(platform, code, uid, oAuthUserInfo.getOpenid());
        return GeneralResponse.generate(200);
    }

    @GetMapping("/info/{platform}/{code}")
    public Map<String, Object> info(@PathVariable("platform") String platform, @PathVariable("code") String code) {
        OAuthUserInfo oAuthUserInfo = oAuthService.callback(platform, code);

        if (oAuthUserInfo == null) {
            return GeneralResponse.generate(404, "Invalid information.");
        } else {
            OAuthInfoResponse response = new OAuthInfoResponse();
            response.setUserInfo(oAuthUserInfo);
            response.setActive(oAuthService.findUid(platform, oAuthUserInfo.getOpenid()) != null);
            return GeneralResponse.generate(200, response);
        }
    }

    @DeleteMapping("/{platform}/{code}")
    public Map<String, Object> delete(@PathVariable("platform") String platform, @PathVariable("code") String code) {
        oAuthService.deleteTokenFromCache(platform, code);
        return GeneralResponse.generate(200);
    }
}
