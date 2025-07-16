package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.UserDTO;
import com.sipcommb.envases.dto.UserRequestDTO;
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

    /**
     * Register the first user
     */
    @PostMapping("/register")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error al registrar el usuario")
    })
    public ResponseEntity<?> registerUser(
            @RequestBody UserRequestDTO userRequestDTO) {
        try {
            UserDTO userDTO = userService.register(
                    userRequestDTO.getUsername(),
                    userRequestDTO.getEmail(),
                    userRequestDTO.getPassword(),
                    userRequestDTO.getFirstName(),
                    userRequestDTO.getLastName(),
                    userRequestDTO.getPhoneNumber(),
                    userRequestDTO.getRoleName()
            );
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", ex.getMessage())); // Handle errors gracefully
        }
    }
}