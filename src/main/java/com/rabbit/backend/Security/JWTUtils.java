package com.rabbit.backend.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Date;
import java.util.List;

public class JWTUtils {
    public static String sign(String uid, String username, Boolean isAdmin) {
        return Jwts.builder()
                .setSubject(uid)
                .claim("username", username)
                .claim("permission", isAdmin ? "Admin,User" : "User")
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS512, System.getenv("SECRET"))
                .compact();
    }

    static UsernamePasswordAuthenticationToken verify(String token) {
        if (token != null) {
            Claims claims = Jwts.parser()
                    .setSigningKey(System.getenv("SECRET"))
                    .parseClaimsJws(token.replace("Bearer ", "")).getBody();

            String uid = claims.getSubject();
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("permission"));

            return uid != null ?
                    new UsernamePasswordAuthenticationToken(uid, claims.get("username"), authorities) :
                    null;
        }
        return null;
    }
}
