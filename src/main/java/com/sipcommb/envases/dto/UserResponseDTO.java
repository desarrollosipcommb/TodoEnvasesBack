package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.User;

public class UserResponseDTO {

    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String roleName;

    public UserResponseDTO() {
    }

    public UserResponseDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fullName = user.getFirstName() + " " + user.getLastName();
        this.phone = user.getPhoneNumber();
        this.roleName = user.getRole() != null ? user.getRole().getName() : null;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    
    
}
