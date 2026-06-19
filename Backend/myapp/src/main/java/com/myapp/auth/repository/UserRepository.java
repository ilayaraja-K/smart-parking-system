package com.myapp.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myapp.auth.entity.User;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email (used for login)
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists (used during registration)
     */
    boolean existsByEmail(String email);
}