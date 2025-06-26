package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.LoginRequest;
import com.sipcommb.envases.dto.LoginResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // TODO: Implement authentication logic
        LoginResponse response = new LoginResponse("dummy-token", loginRequest.getUsername(), "admin", new String[]{"read", "write"});
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // TODO: JWT is stateless, so logout is handled on frontend by removing token
        return ResponseEntity.ok("Logged out successfully");
    }
}
