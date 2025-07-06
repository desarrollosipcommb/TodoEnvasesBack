package com.sipcommb.envases.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.service.CapService;
import com.sipcommb.envases.service.PermissionService;

@RestController
@RequestMapping("/caps")
@CrossOrigin(origins = "*")
public class CapController {
	
    @Autowired
    private CapService capService;

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/add")
    public ResponseEntity<?> addCap(@RequestBody CapRequest capRequest, @RequestHeader("Authorization") String authHeader) {
      
        if(!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear tapas");
        }
        try{
            authHeader = authHeader.replace("Bearer ", "").trim();
            CapDTO capDTO = capService.addCaps(capRequest, authHeader);
            return ResponseEntity.ok(capDTO);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }
}




