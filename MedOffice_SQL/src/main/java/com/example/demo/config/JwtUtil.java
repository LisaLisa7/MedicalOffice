package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;


@Component
public class JwtUtil {

    private String secretKey = "keyyy";

    public Claims extractClaims(String token) throws SignatureException {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new SignatureException("Invalid token");
        }
    }

    public String extractUsername(Claims claims) {
        return claims.get("username",String.class);
    }

    public String extractRole(Claims claims) {
        return (String) claims.get("role",String.class);
    }

    public String extractUserId(Claims claims){
        return claims.get("sub",String.class);
    }

    public UserDetails getUserDetails(Claims claims) {
        String role = claims.get("role", String.class);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
        return new org.springframework.security.core.userdetails.User(claims.getSubject(), "", Collections.singleton(authority));
    }
}
