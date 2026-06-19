package com.myapp.parking.security.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "mySuperSecretKeyForJwtGenerationThatIsAtLeast32Bytes!";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractTokenFromHeader(String header) {
        return header.substring(7);
    }

    public Long getCurrentUserId() {
        String token = getCurrentToken();
        return extractUserId(token);
    }

    public String getCurrentUserRole() {
        return org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority()
                .replace("ROLE_", "");
    }
    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Integer.class).longValue();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }
    public String getCurrentToken() {
        return (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails(); 
    }
    
}