package com.sipcommb.envases.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.service.InformesService;
import com.sipcommb.envases.service.PermissionService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/informes")
@CrossOrigin(origins = "*")
public class InformesController {
    
    @Autowired
    private InformesService informesService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/bodegaInforme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informe generado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no tiene permiso"),
    })
    public ResponseEntity<?> generarInformeBodegas(
            @RequestParam(value = "bodegaNameFilter", required = false) String bodegaNameFilter,
            @RequestParam(value = "itemName", required = false) String itemName,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean cap,
            @RequestParam(defaultValue = "true") boolean jar,
            @RequestParam(defaultValue = "true") boolean quimico,
            @RequestParam(defaultValue = "true") boolean extracto
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para generar informes"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(informesService.generarInformeBodegas(bodegaNameFilter, itemName, pageable, cap, jar, quimico, extracto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/sales/by-client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informe generado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no tiene permiso"),
    })
    public ResponseEntity<?> generarInformeVentasPorCliente(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean completed,
            @RequestParam(defaultValue = "false") boolean cancelled,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para generar informes"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(informesService.generarInformeVentasPorCliente(clientName, startDate, endDate, pageable, completed, cancelled));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/sales/by-item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informe generado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no tiene permiso"),
    })
    public ResponseEntity<?> generarInformeVentasPorItem(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String name, // si queremos buscar un objeto en particular
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean jar, // tipos de items a incluir
            @RequestParam(defaultValue = "true") boolean cap,
            @RequestParam(defaultValue = "true") boolean quimico,
            @RequestParam(defaultValue = "true") boolean extracto,
            @RequestParam(defaultValue = "true") boolean combo
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para generar informes"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(informesService.generarInformeVentasPorItem(name, pageable, jar, cap, quimico, extracto, combo));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

}
