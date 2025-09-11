package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.dto.LoginRequest;
import com.sipcommb.envases.dto.LoginResponse;
import com.sipcommb.envases.service.UserService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try{
            LoginResponse response = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
       
            return ResponseEntity.ok(response);

        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(
                Collections.singletonMap("error", e.getMessage()).toString())); // Handle errors gracefully
        }
        
    }
    
    @PostMapping("/logout")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cierre de sesión exitoso"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    public ResponseEntity<String> logout() {
        // TODO: JWT is stateless, so logout is handled on frontend by removing token
        return ResponseEntity.ok("Logged out successfully");
    }
}
