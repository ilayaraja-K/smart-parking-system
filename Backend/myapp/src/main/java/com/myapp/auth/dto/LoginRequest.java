package com.myapp.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for login request — avoids Jackson 3 deserialization issues with User entity
 */
public class LoginRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
