package com.myapp.auth.service;

import java.util.List;

import com.myapp.auth.entity.User;

/**
 * Service interface for authentication and user management
 */
public interface AuthService {

    /**
     * Register a new user
     */
    User register(User user);

    /**
     * Login user and return JWT token (wrapped in User or handled separately)
     */
    String login(String email, String password);

    /**
     * Get currently logged-in user details
     */
    User getCurrentUser();

    /**
     * Get all users
     */
    List<User> getAllUsers();

    /**
     * Update user role
     */
    User updateUserRole(Long userId, String role);
    
    
    User updateMyProfile(User user);
    
    User getUserById(Long userId);

    User updateUser(Long userId, User user);
}