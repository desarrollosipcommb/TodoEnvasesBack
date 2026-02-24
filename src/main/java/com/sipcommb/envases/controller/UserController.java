package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.dto.UserDTO;
import com.sipcommb.envases.dto.UserRequestDTO;
import com.sipcommb.envases.service.PermissionService;
import com.sipcommb.envases.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Collections;
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
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                Collections.singletonMap("error", ex.getMessage()).toString())); // Handle errors gracefully
        }
    }

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
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para crear administradores"));
        }

        try {
            UserDTO userDTO = userService.registerAdmin(userRequestDTO, authHeader.trim().replace("Bearer ", ""));
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                Collections.singletonMap("error", ex.getMessage()).toString())); // Handle errors gracefully
        }
    }

    @GetMapping("/all")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de usuarios")
    })
    public ResponseEntity<?> getAllUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if(!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas"));
            }

            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(userService.getAllUsers(pageable));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                Collections.singletonMap("error", ex.getMessage()).toString())); // Handle errors gracefully
        }
    }

    @GetMapping("/by-role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios por rol obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de usuarios por rol")
    })
    public ResponseEntity<?> getUsersByRole(
        @RequestParam String roleName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestHeader("Authorization") String authHeader
    ) {
        try {

            if(!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas"));
            }

            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(userService.getUsersByRole(pageable, roleName));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                Collections.singletonMap("error", ex.getMessage()).toString())); // Handle errors gracefully
        }
    }

    @GetMapping("/by-name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios por nombre obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error al obtener la lista de usuarios por nombre")
    })
    public ResponseEntity<?> getUsersByName(
        @RequestParam String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if(!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas"));
            }
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(userService.getByName(pageable, name));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                Collections.singletonMap("error", ex.getMessage()).toString())); // Handle errors gracefully    
        }
    }

    @PutMapping("/delete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error al eliminar el usuario")
    })
    public ResponseEntity<?> deleteUser(
        @RequestParam String username,
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if(!permissionService.hasPermission(authHeader, "delete")) {
                return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para desactivar usuarios"));
            }
            return ResponseEntity.ok(userService.deActivateUser(username));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                Collections.singletonMap("error", ex.getMessage()).toString())); // Handle errors gracefully    
        }
    }

    @PutMapping("/activate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario activado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error al activar el usuario")
    })
    public ResponseEntity<?> activateUser(
        @RequestParam String username,
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if(!permissionService.hasPermission(authHeader, "update")) {
                return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para activar usuarios"));
            }
            return ResponseEntity.ok(userService.activateUser(username));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                Collections.singletonMap("error", ex.getMessage()).toString())); // Handle errors gracefully
        }
    }

    @PutMapping("/update")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error al actualizar el usuario")
    })
    public ResponseEntity<?> updateUser(
        @RequestBody UserRequestDTO userRequestDTO,
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if(!permissionService.hasPermission(authHeader, "update")) {
                return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar usuarios"));
            }
            return ResponseEntity.ok(userService.updateUser(userRequestDTO));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                Collections.singletonMap("error", ex.getMessage()).toString())); // Handle errors gracefully
        }
    }
}