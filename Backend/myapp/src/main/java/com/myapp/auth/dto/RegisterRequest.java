package com.myapp.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for register request — avoids Jackson 3 deserialization issues with User entity
 */
public class RegisterRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    public RegisterRequest() {}

    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
