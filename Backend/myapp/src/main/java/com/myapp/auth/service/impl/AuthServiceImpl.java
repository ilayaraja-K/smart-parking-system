package com.myapp.auth.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myapp.auth.entity.User;
import com.myapp.auth.repository.UserRepository;
import com.myapp.auth.service.AuthService;
import com.myapp.exception.CustomException;
import com.myapp.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of AuthService
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // Argon2 Password Encoder
    private final Argon2PasswordEncoder passwordEncoder;

    /**
     * Register new user
     */
    @Override
    public User register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new CustomException("Email already registered");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default values
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setCreatedOn(LocalDateTime.now());

        // TEMP: set null or 0 before save
        user.setCreatedBy(0L);

        // SAVE FIRST (to generate ID)
        User savedUser = userRepository.save(user);

        // NOW set createdBy = its own ID
        savedUser.setCreatedBy(savedUser.getId());

        return userRepository.save(savedUser);
    }

    /**
     * Login user and return JWT token
     */
    @Override
    public String login(String email, String password) {

        if (email == null || password == null) {
            throw new CustomException("Email and password are required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Invalid email or password"));

        // 🔐 Check account status
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new CustomException("Account is inactive. Contact admin.");
        }

        // 🔐 Validate password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("Invalid email or password");
        }

        // 🔥 Generate JWT
        return jwtUtil.generateToken(user); 
        }
    /**
     * Get current logged-in user
     */
    @Override
    public User getCurrentUser() {

        String email = jwtUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        // NEVER expose password
        user.setPassword(null);

        return user;
    }

    /**
     * Get all users
     */
    @Override
    public List<User> getAllUsers() {

        // 🔐 Only ADMIN allowed
        validateAdminAccess();

        List<User> users = userRepository.findAll();

        users.forEach(u -> u.setPassword(null));

        return users;
    }

    
    @Override
    public User updateMyProfile(User updatedUser) {

        User currentUser = getCurrentUserEntity();

        currentUser.setName(updatedUser.getName());
        currentUser.setEmail(updatedUser.getEmail());

        // Optional: update password
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        currentUser.setUpdatedOn(LocalDateTime.now());
        currentUser.setUpdatedBy(currentUser.getId()); // self update

        return userRepository.save(currentUser);
    }
    /**
     * Update user role
     */
    @Override
    public User updateUserRole(Long userId, String role) {

        // 🔐 Only ADMIN
        validateAdminAccess();

        // ❌ Prevent invalid roles
        if (!"ADMIN".equals(role) && !"USER".equals(role)) {
            throw new CustomException("Invalid role");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));

        // ❌ Prevent admin from demoting himself
        if (user.getId().equals(getCurrentUserId()) && "USER".equals(role)) {
            throw new CustomException("Admin cannot demote himself");
        }

        user.setRole(role);
        user.setUpdatedOn(LocalDateTime.now());
        user.setUpdatedBy(getCurrentUserId());

        return userRepository.save(user);
    }
    
    private User getCurrentUserEntity() {
        String email = jwtUtil.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    private Long getCurrentUserId() {
        return getCurrentUserEntity().getId();
    }

    private void validateAdminAccess() {
        User currentUser = getCurrentUserEntity();

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new CustomException("Access denied: Admin only");
        }
    }
    
    
    
    
    
    
    @Override
    public User getUserById(Long userId) {

        User currentUser = getCurrentUserEntity();

        // 🔐 SECURITY CHECK
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())
                && !currentUser.getId().equals(userId)) {
            throw new CustomException("Access denied");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));

        user.setPassword(null);
        return user;
    }
    
    
    @Override
    public User updateUser(Long userId, User updatedUser) {

        User currentUser = getCurrentUserEntity();

        // 🔐 ONLY SELF ALLOWED
        if (!currentUser.getId().equals(userId)) {
            throw new CustomException("Access denied");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));

        user.setName(updatedUser.getName());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        user.setUpdatedOn(LocalDateTime.now());
        user.setUpdatedBy(userId);

        return userRepository.save(user);
    }
    
}