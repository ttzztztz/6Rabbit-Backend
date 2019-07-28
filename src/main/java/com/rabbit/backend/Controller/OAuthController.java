package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.OAuth.OAuth;
import com.rabbit.backend.Bean.OAuth.OAuthBindKey;
import com.rabbit.backend.Bean.OAuth.OAuthUserInfo;
import com.rabbit.backend.Bean.User.User;
import com.rabbit.backend.Service.FileService;
import com.rabbit.backend.Service.OAuthService;
import com.rabbit.backend.Service.UserService;
import com.rabbit.backend.Utilities.OAuthRedirectURLUtil;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private OAuthService oAuthService;
    private UserService userService;
    private FileService fileService;
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public OAuthController(OAuthService oAuthService, UserService userService, FileService fileService,
                           StringRedisTemplate stringRedisTemplate) {
        this.oAuthService = oAuthService;
        this.userService = userService;
        this.fileService = fileService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @DeleteMapping("/{oid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> delete(@PathVariable("oid") String oid, Authentication authentication) {
        OAuth oAuth = oAuthService.findByOid(oid);
        if (oAuth == null) {
            return GeneralResponse.generate(404, "Not found.");
        }
        String uid = (String) authentication.getPrincipal();
        if (!uid.equals(oAuth.getUid())) {
            return GeneralResponse.generate(403, "No Permission.");
        }

        oAuthService.delete(oid);
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
        response.setHeader("Refresh", "0;URL=" + oAuthService.getURL(platform,
                OAuthRedirectURLUtil.generate(platform)
        ));
    }

    @GetMapping("/{platform}/{token}")
    public void getBindURL(@PathVariable("platform") String platform, @PathVariable("token") String token,
                           HttpServletResponse response) {
        response.setStatus(301);
        response.setHeader("Refresh", "0;URL=" + oAuthService.getURL(platform,
                OAuthRedirectURLUtil.generate(platform, token)
        ));
    }

    @PostMapping("/{platform}")
    public Map<String, Object> callback(@PathVariable("platform") String platform,
                                        HttpServletRequest request) {
        // login & register
        OAuthUserInfo oAuthUserInfo = oAuthService.callback(platform, request);
        OAuth oAuth = oAuthService.findByOpenid(oAuthUserInfo.getOpenid(), platform);

        if (oAuth == null) {
            String key = "oauth:" + platform + ":" + oAuthUserInfo.getOpenid();
            String sessionId = request.getSession().getId();
            stringRedisTemplate.boundValueOps(key).set(sessionId);
            stringRedisTemplate.boundValueOps(key).expire(5 * 60 * 1000, TimeUnit.MILLISECONDS);

            OAuthBindKey oAuthBindKey = new OAuthBindKey();
            oAuthBindKey.setKey(sessionId);
            oAuthBindKey.setOpenid(oAuthUserInfo.getOpenid());
            return GeneralResponse.generate(404, oAuthBindKey);
        } else {
            User user = userService.selectUser("uid", oAuth.getUid());

            String avatarPath = fileService.avatarPath(oAuth.getUid());
            String avatarRemoteAddress = oAuthUserInfo.getAvatarURL();

            fileService.downloadRemoteFile(avatarPath, avatarRemoteAddress);
            return GeneralResponse.generate(200, userService.loginResponse(user));
        }
    }

    @PutMapping("/{platform}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> bind(@PathVariable("platform") String platform, OAuthBindKey oAuthBindKey,
                                    Authentication authentication) {
        //bind
        String uid = (String) authentication.getPrincipal();
        String key = "oauth:" + platform + ":" + oAuthBindKey.getOpenid();
        String sessionID = stringRedisTemplate.boundValueOps(key).get();
        if (sessionID == null || !sessionID.equals(oAuthBindKey.getKey())) {
            return GeneralResponse.generate(400, "Session expired or invalid request.");
        } else {
            OAuth oAuth = new OAuth();
            oAuth.setOpenid(oAuthBindKey.getOpenid());
            oAuth.setPlatform(platform);
            oAuth.setUid(uid);
            oAuthService.insert(oAuth);

            return GeneralResponse.generate(200);
        }
    }

}
