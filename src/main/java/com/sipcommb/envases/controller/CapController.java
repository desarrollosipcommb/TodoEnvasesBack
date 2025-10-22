package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.CapColorRequest;
import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.service.CapService;
import com.sipcommb.envases.service.PermissionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    public ResponseEntity<?> addCap(@RequestBody CapRequest capRequest,
            @RequestHeader("Authorization") String authHeader) {

        if (!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para crear tapas"));
        }
        try {
            authHeader = authHeader.replace("Bearer ", "").trim();
            CapDTO capDTO = capService.addCaps(capRequest, authHeader);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tapas obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de tapas")
    })
    public ResponseEntity<?> getAllCaps(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(capService.getAllCaps(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all/active")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tapas activas obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de tapas activas")
    })
    public ResponseEntity<?> getAllActiveCaps(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas activas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(capService.getAllActiveCaps(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all/inactive")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tapas inactivas obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de tapas inactivas")
    })
    public ResponseEntity<?> getAllInactiveCaps(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas inactivas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(capService.getAllInactiveCaps(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/by-diameter")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tapa obtenida exitosamente por diámetro", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "404", description = "Tapa no encontrada")
    })
    public ResponseEntity<?> getCapByDiameter(
            @RequestBody String diameter,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para leer las tapas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CapDTO> capDTO = capService.getCapsByDiameter(diameter, pageable);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/by-name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tapa obtenida exitosamente por nombre", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "404", description = "Tapa no encontrada")
    })
    public ResponseEntity<?> getCapByName(
            @RequestBody String name,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para leer las tapas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CapDTO> capDTO = capService.getByName(name, pageable);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/update")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tapa actualizada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al actualizar la tapa")
    })
    public ResponseEntity<?> updateCap(@RequestBody CapRequest capRequest,
            @RequestHeader("Authorization") String authHeader) {
        if (!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para actualizar tapas"));
        }
        try {
            authHeader = authHeader.replace("Bearer ", "").trim();
            CapDTO capDTO = capService.updateCap(capRequest, authHeader);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/delete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tapa eliminada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al eliminar la tapa")
    })
    public ResponseEntity<?> deleteCap(@RequestBody CapRequest capRequest,
            @RequestHeader("Authorization") String authHeader) {
        if (!permissionService.hasPermission(authHeader, "delete"))
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para eliminar tapas"));
        try {
            CapDTO capDTO = capService.deleteCap(capRequest);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/activate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tapa activada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al activar la tapa")
    })
    public ResponseEntity<?> activateCap(@RequestBody CapRequest capRequest,
            @RequestHeader("Authorization") String authHeader) {
        if (!permissionService.hasPermission(authHeader, "update"))
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para activar tapas"));
        try {
            CapDTO capDTO = capService.activateCap(capRequest);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/by-name-diameter")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tapa obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "404", description = "Tapa no encontrada")
    })
    public ResponseEntity<?> getCapByName(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String diameter,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para leer las tapas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CapDTO> capDTO = capService.getFromNameLikeAndNameDiameter(name, diameter, pageable);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/by-name-diameter/active")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tapa obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "404", description = "Tapa no encontrada")
    })
    public ResponseEntity<?> getCapByNameActive(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String diameter,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para leer las tapas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CapDTO> capDTO = capService.getFromNameLikeAndNameDiameterActive(name, diameter, pageable);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/add/bodega")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tapa agregada a bodega exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al agregar la tapa a la bodega")
    })
    public ResponseEntity<?> addCapToBodega(@RequestBody CapColorRequest capRequest,
            @RequestHeader("Authorization") String authHeader) {
        if (!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para agregar tapas a bodegas"));
        }
        try {
            CapDTO capDTO = capService.addCapToBodega(capRequest);
            return ResponseEntity.ok(capDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }
}