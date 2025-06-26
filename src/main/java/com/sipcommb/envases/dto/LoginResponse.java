package com.sipcommb.envases.dto;

public class LoginResponse {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private String role;
    private String[] permissions;
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(String token, String username, String role, String[] permissions) {
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
    
    public String[] getPermissions() { return permissions; }
    public void setPermissions(String[] permissions) { this.permissions = permissions; }
}
