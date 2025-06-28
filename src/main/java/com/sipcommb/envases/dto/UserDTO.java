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

}
