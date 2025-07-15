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

import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.dto.InventoryChangeRequest;
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllCaps(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver las tapas");
        }
        try {
            return ResponseEntity.ok(capService.getAllCaps());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all/active")
    public ResponseEntity<?> getAllActiveCaps(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver las tapas activas");
        }
        try {
            return ResponseEntity.ok(capService.getAllActiveCaps());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all/inactive")
    public ResponseEntity<?> getAllInactiveCaps(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver las tapas inactivas");
        }
        try {
            return ResponseEntity.ok(capService.getAllInactiveCaps());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    
    @GetMapping("/by-diameter")
    public ResponseEntity<?> getCapByDiameter(@RequestBody String diameter, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para leer las tapas");
        }
        try {
            List<CapDTO> capDTO = capService.getCapsByDiameter(diameter);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/by-name")
    public ResponseEntity<?> getCapByName(@RequestBody String name, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para leer las tapas");
        }
        try {
            List<CapDTO> capDTO = capService.getByName(name);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/by-color")
    public ResponseEntity<?> getCapByColor(@RequestBody String color, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read"))
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para leer las tapas");
        try {
            List<CapDTO> capDTO = capService.getByColor(color);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateCap(@RequestBody CapRequest capRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")){
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para actualizar tapas");
        }
        try {
            authHeader = authHeader.replace("Bearer ", "").trim();
            CapDTO capDTO = capService.updateCap(capRequest, authHeader);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<?> deleteCap(@RequestBody CapRequest capRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "delete"))
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para eliminar tapas");
        try {
            CapDTO capDTO = capService.deleteCap(capRequest);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/activate")
    public ResponseEntity<?> activateCap(@RequestBody CapRequest capRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update"))
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para activar tapas");
        try {
            CapDTO capDTO = capService.activateCap(capRequest);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/inventory")
    public ResponseEntity<?> updateCapInventory(@RequestBody InventoryChangeRequest inventoryChangeRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update"))
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para actualizar el inventario de tapas");
        try {
            CapDTO capDTO = capService.changeInventory(inventoryChangeRequest.getId(), inventoryChangeRequest.getTransactionType(), inventoryChangeRequest.getQuantity(), inventoryChangeRequest.getDescription(), authHeader);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
