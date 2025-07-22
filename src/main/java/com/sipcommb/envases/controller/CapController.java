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
import com.sipcommb.envases.service.CapService;
import com.sipcommb.envases.service.PermissionService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/caps")
@CrossOrigin(origins = "*")
public class CapController {
	
    @Autowired
    private CapService capService;

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/add")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tapa creada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al crear la tapa")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tapas obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de tapas")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tapas activas obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de tapas activas")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tapas inactivas obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de tapas inactivas")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tapa obtenida exitosamente por diámetro", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "404", description = "Tapa no encontrada")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tapa obtenida exitosamente por nombre", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "404", description = "Tapa no encontrada")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tapa obtenida exitosamente por color", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "404", description = "Tapa no encontrada")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tapa actualizada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar la tapa")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tapa eliminada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al eliminar la tapa")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tapa activada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al activar la tapa")
    })
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventario de tapas actualizado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar el inventario de tapas")
    })
    public ResponseEntity<?> updateCapInventory(@RequestBody CapRequest capRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update"))
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para actualizar el inventario de tapas");
        try {
            CapDTO capDTO = capService.changeInventory(capRequest, authHeader);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
