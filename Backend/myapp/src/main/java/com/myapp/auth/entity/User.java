package com.myapp.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing system users
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id") // 🔥 ADD
	private Long id;

    // Basic user details
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Password should never be exposed in API
    @Column(nullable = false)
    private String password;

    // Role (ADMIN / USER)
    @Column(nullable = false)
    private String role;

    // Status (ACTIVE / INACTIVE)
    @Column(nullable = false)
    private String status;

    // 🔐 Audit Fields (MANDATORY)
    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;

    private Long updatedBy;

    private LocalDateTime updatedOn;
}