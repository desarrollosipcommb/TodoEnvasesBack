package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.User;

public class UserDTO {
    String username;
    String email;
    String role;

    public UserDTO() {}

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().getName();
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
