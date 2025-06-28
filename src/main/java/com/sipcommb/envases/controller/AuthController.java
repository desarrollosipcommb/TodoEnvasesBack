package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.LoginRequest;
import com.sipcommb.envases.dto.LoginResponse;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.service.UserService;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
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
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        //TODO handle exceptions properly
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // TODO: JWT is stateless, so logout is handled on frontend by removing token
        return ResponseEntity.ok("Logged out successfully");
    }
}
