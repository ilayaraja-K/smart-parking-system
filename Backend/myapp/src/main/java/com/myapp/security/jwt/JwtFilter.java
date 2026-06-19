package com.myapp.security.jwt;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.myapp.auth.entity.User;
import com.myapp.auth.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

/**
 * JWT Filter to validate token and set authentication
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository; // ✅ ADDED

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String token = null;
        String email = null;

        // Extract token from header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = jwtUtil.extractTokenFromHeader(authHeader);
            email = jwtUtil.extractEmail(token);
        }

        // If email exists and authentication not set
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
            	//validate token
                if (jwtUtil.validateToken(token, email)) {
                	
                	// 🔥 Fetch user from DB (IMPORTANT)
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                 // 🔐 Get role safely
                    String role = user.getRole() != null ? user.getRole() : "USER";
                 // Create authentication with ROLE_
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.singletonList(
                                            new SimpleGrantedAuthority("ROLE_" + role)
                                    )
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                 // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (Exception e) {
                // 🔥 VERY IMPORTANT: do NOT crash application
                // Just log and continue

                System.out.println("Invalid JWT Token: " + e.getMessage());

                // DO NOT set authentication
                // Let Spring Security handle unauthorized access
            }
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}