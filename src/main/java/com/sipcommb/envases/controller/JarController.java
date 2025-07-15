package com.sipcommb.envases.controller;

import java.util.List;

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

import com.sipcommb.envases.dto.JarDTO;
import com.sipcommb.envases.dto.JarRequestDTO;
import com.sipcommb.envases.dto.UpdateCompatibleCapsRequest;
import com.sipcommb.envases.service.JarService;
import com.sipcommb.envases.service.PermissionService;

@RestController
@RequestMapping("/jars")
@CrossOrigin(origins = "*")
public class JarController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JarService jarService;

    @PostMapping("/add")
    public ResponseEntity<?> addJar(@RequestBody JarRequestDTO jarRequest, @RequestHeader("Authorization") String authHeader) {

        if(!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear frascos");
        }

        try{
            JarDTO response = jarService.addJar(jarRequest, authHeader.replace("Bearer ", "").trim());
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllJars(@RequestHeader("Authorization") String authHeader) {

        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver frascos");
        }

        try{
            List<JarDTO> response = jarService.getAllJars();
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    
    @GetMapping("/all/active")
    public ResponseEntity<?> getAllActiveJars(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver frascos");
        }
        try{
            List<JarDTO> response = jarService.getAllActiveJars();
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all/inactive")
    public ResponseEntity<?> getAllInactiveJars(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver frascos");
        }
        try{
            List<JarDTO> response = jarService.getAllInactiveJars();
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateJar(@RequestBody JarRequestDTO jarRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para actualizar frascos");
        }
        try{
            JarDTO response = jarService.updateJar(jarRequest, authHeader.replace("Bearer ", "").trim());
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update/compatible")
    public ResponseEntity<?> updateJarCompatibleCaps(@RequestBody UpdateCompatibleCapsRequest request, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para actualizar frascos");
        }
        try {
            JarDTO response = jarService.updateCompatible(request.getCaps(), request.getName(),  request.isActive());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<?> deleteJar(@RequestBody String jarName, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "delete")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para eliminar frascos");
        }
        try {
            JarDTO response = jarService.deleteJar(jarName);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/activate")
    public ResponseEntity<?> activateJar(@RequestBody String jarName, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para activar frascos");
        }
        try {
            JarDTO response = jarService.activateJar(jarName);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());

        }
    }

}
