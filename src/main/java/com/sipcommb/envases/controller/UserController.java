package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.UserDTO;
import com.sipcommb.envases.dto.UserRequestDTO;
import com.sipcommb.envases.service.PermissionService;
import com.sipcommb.envases.service.UserService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    /**
     * Register the first user
     */
    @PostMapping("/register")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error al registrar el usuario")
    })
    public ResponseEntity<?> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            UserDTO userDTO = userService.register(userRequestDTO);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", ex.getMessage())); // Handle errors gracefully
        }
    }


    //TODO: test
    @PostMapping("/register-admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador registrado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error al registrar el administrador")
    })
    public ResponseEntity<?> registerAdmin(
        @RequestBody UserRequestDTO userRequestDTO,
        @RequestHeader("Authorization") String authHeader
    ) {
        if (!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear administradores");
        }

        try {
            UserDTO userDTO = userService.registerAdmin(userRequestDTO, authHeader.trim().replace("Bearer ", ""));
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", ex.getMessage())); // Handle errors gracefully
        }
    }
}