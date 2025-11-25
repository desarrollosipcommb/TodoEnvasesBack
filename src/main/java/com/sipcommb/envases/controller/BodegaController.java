package com.sipcommb.envases.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sipcommb.envases.dto.BodegaResponse;
import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.service.BodegaService;
import com.sipcommb.envases.service.PermissionService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/bodegas")
@CrossOrigin(origins = "*")
public class BodegaController {

    @Autowired
    private BodegaService bodegaService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/all")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista paginada de bodegas"),
        @ApiResponse(responseCode = "404", description = "No se encontraron bodegas"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllBodegasPaginated(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver las bodegas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BodegaResponse> bodegasPage = bodegaService.getAllBodegasGrouped(pageable);
            return ResponseEntity.ok(bodegasPage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all/name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de todas las bodegas"),
        @ApiResponse(responseCode = "404", description = "No se encontraron bodegas"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllBodegas(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "") String nameFilter

    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver las bodegas"));
        }
        try {
            List<String> bodegas = bodegaService.getAllBodegas(nameFilter);
            return ResponseEntity.ok(bodegas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/add")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bodega añadida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al añadir la bodega"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> addBodega(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String name,
        @RequestParam Long priority
    ) {
        if(!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para añadir bodegas"));
        }
        try {
            return ResponseEntity.ok(bodegaService.addBodega(name, priority));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/change-priority")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Prioridad cambiada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al cambiar la prioridad"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> changeBodegaPriority(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String name,
        @RequestParam Long newPriority
    ) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para cambiar la prioridad de las bodegas"));
        }
        try {
            return ResponseEntity.ok(bodegaService.changePriority(name, newPriority));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/change-name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nombre cambiado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al cambiar el nombre"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> changeBodegaName(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String oldName,
        @RequestParam String newName
    ) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para cambiar el nombre de las bodegas"));
        }
        try {
            return ResponseEntity.ok(bodegaService.ChangeName(oldName, newName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

}