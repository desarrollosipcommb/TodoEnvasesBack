package com.sipcommb.envases.controller;


import com.sipcommb.envases.dto.ComboRequest;
import com.sipcommb.envases.dto.ComboResponse;
import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.service.ComboService;
import com.sipcommb.envases.service.PermissionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/combos")
@CrossOrigin(origins = "*")
public class ComboController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ComboService comboService;

    @PostMapping("/add")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Combo creado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al crear el combo")
    })
    public ResponseEntity<?> addCombo(@RequestBody ComboRequest comboRequest, @RequestHeader("Authorization") String authHeader) {

        if(!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para crear tapas"));
        }
        try{
            ComboResponse comboResponse = comboService.addCombo(comboRequest);
            return ResponseEntity.ok(comboResponse);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
        
    }

    @GetMapping("/all")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de combos obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllCombos(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver los combos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(comboService.getAllCombos(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/by-name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Combo obtenido exitosamente por nombre", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "404", description = "Combo no encontrado")
    })
    public ResponseEntity<?> getComboByName(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody String comboName
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver los combos"));
        }
        try {
            ComboResponse comboResponse = comboService.getByName(comboName);
            return ResponseEntity.ok(comboResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/like-name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Combo obtenido exitosamente por nombre", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "404", description = "Combo no encontrado")
    })
    public ResponseEntity<?> getComboLikeName(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody String comboName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver los combos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(comboService.getLikeName(comboName, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }
    
    
    @PutMapping("/update")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Combo actualizado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar el combo")
    })    
    public ResponseEntity<?> updateCombo(@RequestBody ComboRequest comboRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar combos"));
        }
        try {
            ComboResponse comboResponse = comboService.updateCombo(comboRequest);
            return ResponseEntity.ok(comboResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/active")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Combo activado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al activar el combo")
    })
    public ResponseEntity<?> toggleComboActive(@RequestBody String comboName, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar combos"));
        }
        try {
            ComboResponse comboResponse = comboService.activeCombo(comboName);
            return ResponseEntity.ok(comboResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/deactivate")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Combo desactivado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al desactivar el combo")
    })
    public ResponseEntity<?> toggleComboInactive(@RequestBody String comboName, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update"))
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar combos"));
        try {
            ComboResponse comboResponse = comboService.deactivateCombo(comboName);
            return ResponseEntity.ok(comboResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all/active")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de combos activos obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de combos activos")
    })
    public ResponseEntity<?> getAllActiveCombos(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver los combos activos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(comboService.getAllActiveCombos(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }

    }

    @GetMapping("/all/inactive")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de combos inactivos obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de combos inactivos")
    })
    public ResponseEntity<?> getAllInactiveCombos(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver los combos inactivos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(comboService.getAllInactiveCombos(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/priceRange")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Combos obtenidos exitosamente por rango de precios", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComboResponse.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener los combos por rango de precios")
    })
    public ResponseEntity<?> getCombosByPriceRange(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestBody PriceSearchRequest priceSearchRequest
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer combos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(comboService.getCombosByPriceRange(priceSearchRequest, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }
}