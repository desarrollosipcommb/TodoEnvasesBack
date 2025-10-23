package com.sipcommb.envases.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sipcommb.envases.service.CapColorService;
import com.sipcommb.envases.dto.BodegaMovementDTO;
import com.sipcommb.envases.dto.CapColorDTO;
import com.sipcommb.envases.dto.CapColorRequest;
import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.service.CapService;
import com.sipcommb.envases.service.PermissionService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/capColor")
@CrossOrigin(origins = "*")
public class CapColorController {

    @Autowired
    private CapService capService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private CapColorService capColorService;


    @PostMapping("/add")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Color de tapa agregado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al agregar el color de la tapa")
    })
    public ResponseEntity<?> addCapColor(@RequestBody CapColorRequest capColorRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para agregar colores a las tapas"));
        }
        try {
            authHeader = authHeader.replace("Bearer ", "").trim();
            CapDTO capDTO = capService.addCapColor(capColorRequest, authHeader);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all/colors")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de colores de tapa obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapColorDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de colores de tapa")
    })
    public ResponseEntity<?> getAllCapColors(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "") String name,
        @RequestParam(defaultValue = "") String diameter,
        @RequestParam(defaultValue = "") String color,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver los colores de las tapas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CapColorDTO> capColorDTO = capService.getAllCapColor(new CapRequest(name, "", diameter), color, pageable);
            return ResponseEntity.ok(capColorDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all/active/colors")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de colores de tapa activa obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapColorDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de colores de tapa activa")
    })
    public ResponseEntity<?> getAllActiveCapColors(
        @RequestBody CapRequest capRequest,
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "") String color,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver los colores de las tapas activas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CapColorDTO> capColorDTO = capService.getAllActiveCapColor(capRequest, color, pageable);
            return ResponseEntity.ok(capColorDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/activate")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Color de tapa activado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapColorDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al activar el color de tapa")
    })
    public ResponseEntity<?> activateCapColor(
        @RequestBody CapColorRequest capColorRequest,
        @RequestHeader("Authorization") String authHeader
    ) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para activar colores de tapas"));
        }
        try {
            CapColorDTO capColorDTO = capColorService.activateCapColor(capColorRequest);
            return ResponseEntity.ok(capColorDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/deactivate")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Color de tapa desactivado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapColorDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al desactivar el color de tapa")
    })
    public ResponseEntity<?> deactivateCapColor(
        @RequestBody CapColorRequest capColorRequest,
        @RequestHeader("Authorization") String authHeader
    ) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para desactivar colores de tapas"));
        }
        try {
            CapColorDTO capColorDTO = capColorService.deactivateCapColor(capColorRequest);
            return ResponseEntity.ok(capColorDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        } 
    }

    @PutMapping("/update")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Color de tapa actualizado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar el color de tapa")
    })
    public ResponseEntity<?> updateCap(
        @RequestBody CapColorRequest capColorRequest,
        @RequestHeader("Authorization") String authHeader
    ) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar colores de tapas"));
        }
        try {
            authHeader = authHeader.replace("Bearer ", "").trim();
            CapDTO capDTO = capService.updateColorCap(capColorRequest, authHeader);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/change_inventory")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cantidad de color de tapa actualizada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapColorDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar la cantidad de color de tapa")
    })
    public ResponseEntity<?> updateCapColorQuantity(
        @RequestBody CapColorRequest capColorRequest,
        @RequestHeader("Authorization") String authHeader
    ) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar la cantidad de colores de tapas"));
        }
        try {
            authHeader = authHeader.replace("Bearer ", "").trim();
            CapDTO capDTO = capService.changeInventory(capColorRequest, authHeader);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/bodega_transfer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transferencia de inventario entre bodegas realizada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapColorDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al realizar la transferencia de inventario")
    })
    public ResponseEntity<?> transferInventoryBetweenBodegas(
        @RequestBody BodegaMovementDTO bodegaMovementDTO,
        @RequestHeader("Authorization") String authHeader
    ) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para transferir inventario entre bodegas"));
        }
        try {
            authHeader = authHeader.replace("Bearer ", "").trim();
            CapColorDTO capColorDTO = capColorService.BodegaTransfer(bodegaMovementDTO);
            return ResponseEntity.ok(capColorDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

}