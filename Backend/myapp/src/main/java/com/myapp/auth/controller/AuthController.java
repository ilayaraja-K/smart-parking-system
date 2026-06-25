package com.myapp.auth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myapp.auth.entity.User;
import com.myapp.auth.dto.LoginRequest;
import com.myapp.auth.dto.RegisterRequest;
import com.myapp.auth.dto.UpdateUserRequest;
import com.myapp.auth.service.AuthService;
import com.myapp.common.AppResponse;
import com.myapp.common.MyServiceMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "User Management", description = "APIs for user operations")
@RestController
@RequestMapping("/uvmgmt/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register user
     */
    @Operation(summary = "Register a user")
    @PostMapping("/register")
    public ResponseEntity<AppResponse<User>> register(@RequestBody RegisterRequest req) {

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());

        User savedUser = authService.register(user);
        savedUser.setPassword(null);

        return ResponseEntity.ok(
                AppResponse.createSuccessfullyCreatedMessage(
                        MyServiceMessage.CREATED,
                        savedUser
                )
        );
    }

    /**
     * Login user
     */
    @Operation(summary = "Login a User")
    @PostMapping("/login")
    public ResponseEntity<AppResponse<Map<String, String>>> login(@RequestBody LoginRequest req) {

        String token = authService.login(req.getEmail(), req.getPassword());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        response
                )
        );
    }

    /**
     * 🔥 Get user by ID (SELF + ADMIN)
     */
    @Operation(summary = "Get User by UserId")
    @GetMapping("/{userId}")
    public ResponseEntity<AppResponse<User>> getUserById(@PathVariable Long userId) {

        User user = authService.getUserById(userId);
        user.setPassword(null);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        user
                )
        );
    }

    /**
     * 🔥 Update user profile (SELF only)
     */
    @Operation(summary = "Update the User")
    @PutMapping("/{userId}")
    public ResponseEntity<AppResponse<User>> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest req) {

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());

        User updatedUser = authService.updateUser(userId, user);
        updatedUser.setPassword(null);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        updatedUser
                )
        );
    }

    /**
     * ADMIN: Get all users
     */
    @Operation(summary = "Get For Admin")
    @GetMapping
    public ResponseEntity<AppResponse<List<User>>> getAllUsers() {

        List<User> users = authService.getAllUsers();

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        users
                )
        );
    }

    /**
     * ADMIN: Update role
     */
    @Operation(summary = "Admin can update a users role")
    @PutMapping("/{userId}/role")
    public ResponseEntity<AppResponse<User>> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {

        String role = request.get("role");

        User updatedUser = authService.updateUserRole(userId, role);

        return ResponseEntity.ok(
                AppResponse.createSuccessMessage(
                        MyServiceMessage.SUCCESS,
                        updatedUser
                )
        );
    }

    @GetMapping("/ping")
public String ping() {
    return "AUTH SERVICE OK";
}
}