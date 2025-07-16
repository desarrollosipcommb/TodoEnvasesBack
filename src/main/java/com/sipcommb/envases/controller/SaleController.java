package com.sipcommb.envases.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sipcommb.envases.dto.SaleRequest;
import com.sipcommb.envases.service.PermissionService;
import com.sipcommb.envases.service.SaleService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/sale")
@CrossOrigin(origins = "*")
public class SaleController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SaleService saleService; 
    

    @PostMapping("/add")
    public ResponseEntity<?> addSale(@RequestHeader("Authorization") String authHeader, @RequestBody SaleRequest sale) {
        if(!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear ventas");
        }

        try{
            saleService.addSale(sale, authHeader.trim().replace("Bearer ", ""));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }



        return ResponseEntity.ok("Venta agregada exitosamente");
    }
    
}
