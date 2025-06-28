package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.UserDTO;
import com.sipcommb.envases.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Register the first user
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String roleName) {
        try {
            UserDTO userDTO = userService.register(username, email, password, firstName, lastName, roleName);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(null); // Handle errors gracefully
        }
    }
}