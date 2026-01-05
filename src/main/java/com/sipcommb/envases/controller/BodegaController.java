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
        System.out.println("Fetching all bodegas with filter: " + nameFilter);
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver las bodegas"));
        }
        try {
            List<String> bodegas = bodegaService.getAllBodegas(nameFilter);
            return ResponseEntity.ok(bodegas);
        } catch (Exception e) {
            System.out.println("Error fetching bodegas: " + e.getMessage());
            e.printStackTrace();
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

    @PutMapping("/update")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bodega actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar la bodega"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> updateBodega(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String newName,
        @RequestParam(required = false) Long priority
    ) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar las bodegas"));
        }
        try {
            return ResponseEntity.ok(bodegaService.update(newName, name, priority));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }


    @GetMapping("/like-name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de bodegas que coinciden con el filtro"),
        @ApiResponse(responseCode = "400", description = "Error al obtener las bodegas"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getBodegasLikeName(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(required = false) String nameFilter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver las bodegas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BodegaResponse> bodegasPage = bodegaService.getBodegas(pageable, nameFilter);
            return ResponseEntity.ok(bodegasPage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/specific")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bodega obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la bodega"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getSpecificBodega(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String name,
        @RequestParam(defaultValue = "") String itemFilter
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver las bodegas"));
        }
        try {
            return ResponseEntity.ok(bodegaService.getSpecific(name, itemFilter));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/transfer-inventory")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventario transferido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al transferir el inventario"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> transferInventoryBetweenBodegas(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String fromBodegaName,
        @RequestParam String toBodegaName,
        @RequestParam String item,
        @RequestParam int quantity
    ) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para transferir inventario entre bodegas"));
        }
        try {
            return ResponseEntity.ok(bodegaService.transferInventory(fromBodegaName, toBodegaName, item, quantity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        } 
    }

}