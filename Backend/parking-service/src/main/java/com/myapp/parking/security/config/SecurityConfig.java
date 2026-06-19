package com.myapp.parking.security.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.myapp.parking.security.jwt.*;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

            	    // 🔐 ADMIN ONLY
            	    .requestMatchers("/pabsm/buildings/create").hasRole("ADMIN")
            	    .requestMatchers("/pabsm/slots").hasRole("ADMIN")

            	    // 🔐 USER ACCESS
            	    .requestMatchers("/pabsm/bookings/**").authenticated()
            	    .requestMatchers("/pabsm/slots/**").authenticated()
            	    .requestMatchers("/pabsm/buildings/**").authenticated()
            	    .requestMatchers("/pabsm/users/**").authenticated()
            	    .requestMatchers(
            	    	    "/swagger-ui/**",
            	    	    "/swagger-ui.html",
            	    	    "/v3/api-docs/**"
            	    	).permitAll()
            	    .anyRequest().authenticated()
            	)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}