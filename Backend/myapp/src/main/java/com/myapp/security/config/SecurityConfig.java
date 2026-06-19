package com.myapp.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.myapp.security.jwt.JwtAuthEntryPoint;
import com.myapp.security.jwt.JwtFilter;

import lombok.RequiredArgsConstructor;

/**
 * Security Configuration (UPDATED for uvmgmt APIs)
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
  
            .csrf(csrf -> csrf.disable())

            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // ✅ PUBLIC APIs
                .requestMatchers("/uvmgmt/users/register", "/uvmgmt/users/login").permitAll()

                // 🔐 ADMIN APIs
                .requestMatchers("/uvmgmt/users").hasRole("ADMIN")
                .requestMatchers("/uvmgmt/users/*/role").hasRole("ADMIN")
                .requestMatchers("/uvmgmt/vehicles").hasRole("ADMIN")
                .requestMatchers("/uvmgmt/vehicles/*/users").hasRole("ADMIN")

                // 🔐 ALL OTHER APIs REQUIRE AUTH
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}