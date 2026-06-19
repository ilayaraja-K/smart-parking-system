package com.myapp.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for update user profile request
 */
public class UpdateUserRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    public UpdateUserRequest() {}

    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
