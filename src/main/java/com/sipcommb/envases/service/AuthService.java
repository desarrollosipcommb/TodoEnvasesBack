package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.LoginRequest;
import com.sipcommb.envases.dto.LoginResponse;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Authenticate user and generate JWT token
     */
    public LoginResponse authenticate(LoginRequest loginRequest) {
        // 1. Find user by username
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("Invalid username or password");
        }
        
        User user = userOptional.get();
        
        // 2. Check if user is active
        if (!user.getIsActive()) {
            throw new BadCredentialsException("Account is disabled");
        }
        
        // 3. Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        
        // 4. Generate JWT token
        String token = jwtService.generateToken(user);
        
        // 5. Get user permissions from role
        String[] permissions = getUserPermissions(user);
        
        // 6. Return response
        return new LoginResponse(
            token,
            user.getUsername(),
            user.getRole().getName(),
            permissions
        );
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        return jwtService.getUsernameFromToken(token);
    }

    /**
     * Extract user permissions from role
     */
    private String[] getUserPermissions(User user) {
        // TODO: Parse JSON permissions from role or return default permissions
        String roleName = user.getRole().getName();
        
        switch (roleName.toLowerCase()) {
            case "admin":
                return new String[]{"read", "write", "delete", "manage_users", "manage_inventory"};
            case "seller":
                return new String[]{"read", "create_sale", "view_inventory"};
            case "manager":
                return new String[]{"read", "write", "manage_inventory", "view_reports"};
            default:
                return new String[]{"read"};
        }
    }
}
