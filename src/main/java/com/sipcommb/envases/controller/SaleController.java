package com.sipcommb.envases.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.sipcommb.envases.dto.SaleDTO;
import com.sipcommb.envases.dto.SaleRequest;
import com.sipcommb.envases.service.PermissionService;
import com.sipcommb.envases.service.SaleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/sale")
@CrossOrigin(origins = "*")
public class SaleController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SaleService saleService; 
    

    // Se usa para crear una venta y guardarla en la base de datos
    @Operation(
        summary = "Crear una venta", 
        description = "Permite crear una venta y guardarla en la base de datos. Se deberia correr el plan antes de crear la venta para evitar errores. \\n\\n" + //
                        "Quiero resaltar que cada SaleRequest tiene una lista de SaleItemRequest, este solo puede tener un nombre especificado, es decir \\n" + //
                        "Si se llena capName y jarName al tiempo va a soltar error")
    @PostMapping("/add")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta creada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    public ResponseEntity<?> addSale(@RequestHeader("Authorization") String authHeader, @RequestBody SaleRequest sale) {
        if(!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear ventas");
        }

    try{
        SaleDTO saleDTO = saleService.addSale(sale, authHeader.trim().replace("Bearer ", ""));
        return ResponseEntity.ok(saleDTO);
    }catch (Exception e) {
        
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
}


    @Operation(
        summary = "Planificar una venta", 
        description = "Permite planificar una venta sin guardarla en la base de datos para ver el precio total y los detalles de la venta.  \\n\\n" + //
                        "Quiero resaltar que cada SaleRequest tiene una lista de SaleItemRequest, este solo puede tener un nombre especificado, es decir \\n" + //
                        "Si se llena capName y jarName al tiempo va a soltar error")
    @PostMapping("/plan")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta planificada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    public ResponseEntity<?> planSale(@RequestHeader("Authorization") String authHeader, @RequestBody SaleRequest sale) {
        if(!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear ventas");
        }

        try{
            SaleDTO saleDTO = saleService.planSale(sale, authHeader.trim().replace("Bearer ", ""));
            return ResponseEntity.ok(saleDTO);
        }catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            cause.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + cause.getMessage());
        }
             
    }

    @GetMapping("/all")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de ventas")
    })
    public ResponseEntity<?> getAllSales(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para leer las ventas");
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(saleService.getAllSales(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener las ventas: " + e.getMessage());
        }
    }

    @GetMapping("/by-email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de ventas del usuario obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de ventas del usuario")
    })
    public ResponseEntity<?> getSalesByUser(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody String email,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para leer las ventas");
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(saleService.getSalesByEmail(email, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/by-username")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de ventas del usuario obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de ventas del usuario")
    })
    public ResponseEntity<?> getSalesByUsername(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody String username,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para leer las ventas");
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(saleService.getSalesByUsername(username, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
