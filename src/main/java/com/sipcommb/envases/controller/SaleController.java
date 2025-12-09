package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.dto.SaleDTO;
import com.sipcommb.envases.dto.SaleRequest;
import com.sipcommb.envases.service.PermissionService;
import com.sipcommb.envases.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.math.BigDecimal;

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
    public ResponseEntity<?> addSale(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody SaleRequest sale,
        @RequestParam(defaultValue = "") String clientName,  
        @RequestParam(defaultValue = "") String clientDocument,
        @RequestParam(defaultValue = "") String clientPhone,
        @RequestParam(defaultValue = "") String clientAddress
    ) {
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para crear ventas"));
        }

        try {
            SaleDTO saleDTO = saleService.addSale(sale, authHeader.trim().replace("Bearer ", ""), true, clientName, clientDocument, clientPhone, clientAddress);
            return ResponseEntity.ok(saleDTO);
        } catch (Exception e) {

            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
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
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para crear ventas"));
        }

        try {
            SaleDTO saleDTO = saleService.addSale(sale, authHeader.trim().replace("Bearer ", ""), false);
            return ResponseEntity.ok(saleDTO);
        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            cause.printStackTrace();
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + cause.getMessage()));
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
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer las ventas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(saleService.getAllSales(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new CustomApiResponse("Error al obtener las ventas: " + e.getMessage()));
        }
    }

    @GetMapping("/like-sellerName-range")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de ventas")
    })
    public ResponseEntity<?> getAllSales(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam() String fechaInicio,
            @RequestParam() String fechaFin,
            @RequestParam(required = false) String sellerName,
            @RequestParam(required = false) String clientName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer las ventas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(saleService.getFindByFechaAndVendedor(fechaInicio, fechaFin, sellerName, clientName, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new CustomApiResponse("Error al obtener las ventas: " + e.getMessage()));
        }
    }


    //TODO: es posible que esto necesite cambios cuando se puedan editar ventas
    @GetMapping("/like-sellerName-range/total")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total de ventas obtenido exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al obtener el total de ventas")
    })
    public ResponseEntity<?> getTotalSales(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam() String fechaInicio,
            @RequestParam() String fechaFin,
            @RequestParam(required = false) String sellerName
    ) {
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer las ventas"));
        }
        try {
            return ResponseEntity.ok(saleService.getTotalAmountByFechaAndVendedor(fechaInicio, fechaFin, sellerName));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new CustomApiResponse("Error al obtener el total de ventas: " + e.getMessage()));
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
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer las ventas"));
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
            @ApiResponse(responseCode = "200", description = "Lista de ventas del vendedor obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de ventas del usuario")
    })
    public ResponseEntity<?> getSalesByUsername(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer las ventas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(saleService.getSalesByUsername(username, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/priceRange")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ventas por rango de precio obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de ventas por rango de precio")
    })
    public ResponseEntity<?> getPriceRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PriceSearchRequest priceRangeRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer las ventas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(saleService.getPriceRange(priceRangeRequest, pageable));
        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            return ResponseEntity.status(500).body(new CustomApiResponse("Error al obtener las ventas por rango de precio: " + cause.getMessage()));
        }
    }

    @PutMapping("/deactivate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta desactivada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al desactivar la venta")
    })
    public ResponseEntity<?> deactivateSale(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long id
    ) {
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para desactivar ventas"));
        }
        try {
            SaleDTO saleDTO = saleService.deactivateSale(id);
            return ResponseEntity.ok(saleDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new CustomApiResponse("Error al desactivar la venta: " + e.getMessage()));
        }
    }

    @GetMapping("/byClient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ventas por cliente obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaleDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de ventas por cliente")
    })
    public ResponseEntity<?> getSalesByClient(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String clientName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (!permissionService.hasPermission(authHeader, "sales")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer las ventas"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(saleService.getSalesByClient(pageable, clientName));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new CustomApiResponse("Error al obtener las ventas por cliente: " + e.getMessage()));
        }
    }

}