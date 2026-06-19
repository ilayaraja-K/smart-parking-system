package com.myapp.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import com.myapp.auth.entity.User;
import com.myapp.auth.repository.UserRepository;
import com.myapp.exception.CustomException;
import com.myapp.security.jwt.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Argon2PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@gmail.com")
                .password("password")
                .role("USER")
                .status("ACTIVE")
                .createdBy(1L)
                .createdOn(LocalDateTime.now())
                .build();
    }

    // ================= REGISTER =================

    @Test
    void testRegister_Success() {

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        User result = authService.register(user);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void testRegister_EmailAlreadyExists() {

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(CustomException.class, () -> {
            authService.register(user);
        });
    }

    // ================= LOGIN =================

    @Test
    void testLogin_Success() {

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", user.getPassword()))
                .thenReturn(true);

        when(jwtUtil.generateToken(user)).thenReturn("mockToken");

        String token = authService.login(user.getEmail(), "password");

        assertEquals("mockToken", token);
    }

    @Test
    void testLogin_InvalidPassword() {

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", user.getPassword()))
                .thenReturn(false);

        assertThrows(CustomException.class, () -> {
            authService.login(user.getEmail(), "wrong");
        });
    }

    // ================= GET USER =================

    @Test
    void testGetUserById_Success() {

        when(jwtUtil.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = authService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}