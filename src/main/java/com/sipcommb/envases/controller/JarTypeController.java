package com.sipcommb.envases.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sipcommb.envases.dto.JarTypeDTO;

import com.sipcommb.envases.service.JarTypeService;
import com.sipcommb.envases.service.PermissionService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/jar-types")
@CrossOrigin(origins = "*")
public class JarTypeController {

    @Autowired
    private JarTypeService jarTypeService;

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/add")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tipo de tapa creado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarTypeDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al crear el tipo de tapa")
    })
    public ResponseEntity<?> addJarType(@RequestBody JarTypeDTO jarTypeDTO, @RequestHeader("Authorization") String authHeader) {

        try {

            if(!permissionService.hasPermission(authHeader, "create")) {
                return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear tapas");
            }

            JarTypeDTO response = jarTypeService.addJarTypes(jarTypeDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Algo salio mal: " + e.getMessage());
        }
    }

    @GetMapping("/by-diameter")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tipo de tapa obtenido exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarTypeDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener el tipo de tapa")
    })
    public ResponseEntity<?> getJarTypeByDiameter(@RequestBody String diameter, @RequestHeader("Authorization") String authHeader) {
        try {
            if(!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body("Este usuario no tiene permiso para leer los tipos de tapas");
            }
            JarTypeDTO jarTypeDTO = jarTypeService.getByDiameter(diameter);
            return ResponseEntity.ok(jarTypeDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tipos de tapa obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarTypeDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de tipos de tapa")
    })
    public ResponseEntity<?> getAllJarTypes(@RequestHeader("Authorization") String authHeader) {
        try {
            if(!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body("Este usuario no tiene permiso para leer los tipos de tapas");
            }
            return ResponseEntity.ok(jarTypeService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
