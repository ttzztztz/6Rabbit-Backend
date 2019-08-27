package com.rabbit.backend.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CheckAuthority {
    public static Boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority(authority));
    }
}
