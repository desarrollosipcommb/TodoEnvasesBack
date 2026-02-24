package com.sipcommb.envases.dto;

import java.util.Set;

public class LoginResponse {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private String role;
    private Set<String> permissions;
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(String token, String username, String role, Set<String> permissions) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.permissions = permissions;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
}
