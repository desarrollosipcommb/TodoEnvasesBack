package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.LoginRequest;
import com.sipcommb.envases.dto.LoginResponse;
import com.sipcommb.envases.service.UserService;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try{
            LoginResponse response = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
       
            return ResponseEntity.ok(response);

        }catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); // Handle errors gracefully
        }
        
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // TODO: JWT is stateless, so logout is handled on frontend by removing token
        return ResponseEntity.ok("Logged out successfully");
    }
}
