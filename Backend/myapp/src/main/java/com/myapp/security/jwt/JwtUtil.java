package com.myapp.security.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;



import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

/**
 * Utility class for handling JWT operations
 */
@Component
public class JwtUtil {

    // Secret key (should be moved to application.properties in real projects)
    private static final String SECRET_KEY = "mySuperSecretKeyForJwtGenerationThatIsAtLeast32Bytes!";

    // Token validity (e.g., 1 day)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Generate JWT token using email as subject
     */
    public String generateToken(com.myapp.auth.entity.User user) {

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())     // ✅ now valid
                .claim("role", user.getRole())     // ✅ now valid
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Extract email (username) from token
     */
    public String extractEmail(String token) {

        return getClaims(token).getSubject();
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token, String email) {

        final String extractedEmail = extractEmail(token);

        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {

        return getClaims(token).getExpiration().before(new Date());
    }

    /**
     * Extract all claims from token
     */
    private Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extract token from Authorization header (Bearer token)
     */
    public String extractTokenFromHeader(String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Placeholder method to get current logged-in user's email
     * (will work after integrating JwtFilter + SecurityContext)
     */
    public String getCurrentUserEmail() {

        // This will be populated after SecurityContext integration
        return org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}