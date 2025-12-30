package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.service.FileService;
import com.sipcommb.envases.service.PermissionService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private FileService fileService;

    @PostMapping("/upload-excel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo subido y procesado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, por ejemplo, archivo no proporcionado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no tiene permiso"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar el archivo")
    })
    public ResponseEntity<?> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("No se ha subido ningún archivo"));
        }
        try {
            if (!permissionService.hasPermission(authHeader, "create")) {
                return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para subir inventario"));
            }
            return ResponseEntity.ok(fileService.readFile(file, authHeader));
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return ResponseEntity.status(500).body(new CustomApiResponse("Error al subir el archivo: " + e.getMessage()));
        }
    }

    @PostMapping("/update-inventory-excel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo subido y procesado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, por ejemplo, archivo no proporcionado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no tiene permiso"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar el archivo")
    })
    public ResponseEntity<?> updateInventoryByExcel(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("No se ha subido ningún archivo"));
        }

        try {
            if (!permissionService.hasPermission(authHeader, "create")) {
                return ResponseEntity.status(403).body(
                        new CustomApiResponse("Este usuario no tiene permiso para actualizar el inventario"));
            }

            return ResponseEntity.ok(fileService.readFileInventory(file, authHeader));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new CustomApiResponse("Error al subir el archivo: " + e.getMessage()));
        }
    }
}
