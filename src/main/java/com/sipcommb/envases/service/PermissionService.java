package com.sipcommb.envases.service;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    @Autowired
    private JwtService jwtService;

    public boolean hasPermission(String token, String permission) {
        token = token.replace("Bearer ", "").trim();
        
        Set<String> permissions = jwtService.getAuthoritiesFromToken(token);
        return permissions.contains(permission);
    }
}