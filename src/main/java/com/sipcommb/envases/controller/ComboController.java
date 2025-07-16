package com.sipcommb.envases.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sipcommb.envases.dto.ComboRequest;
import com.sipcommb.envases.dto.ComboResponse;
import com.sipcommb.envases.service.ComboService;
import com.sipcommb.envases.service.PermissionService;

@RestController
@RequestMapping("/combos")
@CrossOrigin(origins = "*")
public class ComboController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ComboService comboService;

    @PostMapping("/add")
    public ResponseEntity<?> addCombo(@RequestBody ComboRequest comboRequest, @RequestHeader("Authorization") String authHeader) {

        if(!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear tapas");
        }
        try{
            ComboResponse comboResponse = comboService.addCombo(comboRequest);
            return ResponseEntity.ok(comboResponse);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
        
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCombos(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver los combos");
        }
        try {
            return ResponseEntity.ok(comboService.getAllCombos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/by-name")
    public ResponseEntity<?> getComboByName(@RequestHeader("Authorization") String authHeader, @RequestBody String comboName) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver los combos");
        }
        try {
            ComboResponse comboResponse = comboService.getByName(comboName);
            return ResponseEntity.ok(comboResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCombo(@RequestBody ComboRequest comboRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para actualizar combos");
        }
        try {
            ComboResponse comboResponse = comboService.updateCombo(comboRequest);
            return ResponseEntity.ok(comboResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/active")
    public ResponseEntity<?> toggleComboActive(@RequestBody String comboName, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para actualizar combos");
        }
        try {
            ComboResponse comboResponse = comboService.activeCombo(comboName);
            return ResponseEntity.ok(comboResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/deactivate")
    public ResponseEntity<?> toggleComboInactive(@RequestBody String comboName, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update"))
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para actualizar combos");
        try {
            ComboResponse comboResponse = comboService.deactivateCombo(comboName);
            return ResponseEntity.ok(comboResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all/active")
    public ResponseEntity<?> getAllActiveCombos(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver los combos activos");
        }
        try {
            return ResponseEntity.ok(comboService.getAllActiveCombos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }

    @GetMapping("/all/inactive")
    public ResponseEntity<?> getAllInactiveCombos(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver los combos inactivos");
        }
        try {
            return ResponseEntity.ok(comboService.getAllInactiveCombos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}