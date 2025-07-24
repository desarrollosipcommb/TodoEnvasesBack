package com.sipcommb.envases.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import com.sipcommb.envases.dto.TransactionResponseDTO;
import com.sipcommb.envases.service.InventoryService;
import com.sipcommb.envases.service.PermissionService;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private PermissionService permissionService;

    

    @GetMapping("/get/all")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener las transacciones")
    })
    public ResponseEntity<?> getAllTransactions(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
        ) {
        try {
            if (!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver transacciones");
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionResponseDTO> transactions = inventoryService.getAll(pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Algo salió mal: " + e.getMessage());  
        }
    }

    @GetMapping("/get/itemType")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones por tipo de item obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener las transacciones por tipo de item")
    })
    public ResponseEntity<?> getTransactionsByItemType(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody String itemType,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
        ) {
        try {
            if (!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver transacciones");
            }
            Pageable pageable = PageRequest.of(page, size);

            Page<TransactionResponseDTO> transactions = inventoryService.getByItemType(pageable, itemType);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Algo salió mal: " + e.getMessage());
        }
    }

    @GetMapping("/get/transactionType")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones por tipo de transacción obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener las transacciones por tipo de transacción")
    })
    public ResponseEntity<?> getTransactionsByTransactionType(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody String transactionType,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        try {
            if (!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver transacciones");
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionResponseDTO> transactions = inventoryService.getByTransactionType(pageable, transactionType);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Algo salió mal: " + e.getMessage());
        }
    }

    @GetMapping("/get/email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones por usuario obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener las transacciones por usuario")
    })
    public ResponseEntity<?> getTransactionsByUser(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody String email,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
        ) {
        try {
            if (!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver transacciones");
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionResponseDTO> transactions = inventoryService.getByEmail(pageable, email);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Algo salió mal: " + e.getMessage());
        }
    }

    @GetMapping("/get/username")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones por usuario obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener las transacciones por usuario")
    })
    public ResponseEntity<?> getTransactionsByUsername(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody String username,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
        ) {
        try {
            if (!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver transacciones");
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionResponseDTO> transactions = inventoryService.getByUsername(pageable, username);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Algo salió mal: " + e.getMessage());
        }
    }
}