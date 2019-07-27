package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.OAuth.OAuth;
import com.rabbit.backend.Service.OAuthService;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private OAuthService oAuthService;

    @Autowired
    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @DeleteMapping("/{oid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> delete(@PathVariable("oid") String oid, Authentication authentication) {
        OAuth oAuth = oAuthService.find(oid);
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
}
