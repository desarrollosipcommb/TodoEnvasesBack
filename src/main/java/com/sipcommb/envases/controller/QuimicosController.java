package com.sipcommb.envases.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.sipcommb.envases.dto.QuimicosDTO;
import com.sipcommb.envases.service.PermissionService;
import com.sipcommb.envases.service.QuimicosService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/quimicos")
@CrossOrigin(origins = "*")
public class QuimicosController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private QuimicosService quimicosService;


    @PostMapping("/add")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quimico agregado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> addQuimico(@RequestHeader("Authorization") String authHeader, @RequestBody QuimicosDTO quimicoDTO) {
         if(!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear frascos");
        }

        try{
            QuimicosDTO newQuimico = quimicosService.addQuimico(quimicoDTO);
            return ResponseEntity.ok(newQuimico);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error al agregar el quimico: " + e.getMessage());
        }
        
    }

    @GetMapping("/all")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de quimicos obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllQuimicos(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver frascos");
        }

        try{
            List<QuimicosDTO> quimicos = quimicosService.getAllQuimicos();
            return ResponseEntity.ok(quimicos);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error al obtener los quimicos: " + e.getMessage());
        }
    }

    @GetMapping("/all/active")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quimicos activos obtenidos exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllActiveQuimicos(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver frascos");
        }

        try{
            List<QuimicosDTO> quimicos = quimicosService.getActiveQuimicos();
            return ResponseEntity.ok(quimicos);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error al obtener los quimicos activos: " + e.getMessage());
        }
    }

    @GetMapping("/all/inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quimicos inactivos obtenidos exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllInactiveQuimicos(@RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para ver frascos");
        }

        try{
            List<QuimicosDTO> quimicos = quimicosService.getInactiveQuimicos();
            return ResponseEntity.ok(quimicos);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error al obtener los quimicos inactivos: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quimico actualizado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> updateQuimico(@RequestHeader("Authorization") String authHeader, @RequestBody QuimicosDTO quimicoDTO) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para actualizar frascos");
        }   

        try{
            QuimicosDTO updatedQuimico = quimicosService.updateQuimico(quimicoDTO, authHeader.trim().replace("Bearer ", ""));
            return ResponseEntity.ok(updatedQuimico);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error al actualizar el quimico: " + e.getMessage());
        }
    }

    @PutMapping("/delete")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quimico eliminado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> deleteQuimico(@RequestHeader("Authorization") String authHeader, @RequestBody QuimicosDTO quimicoDTO) {
        if(!permissionService.hasPermission(authHeader, "delete")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para eliminar frascos");
        }

        try{
            QuimicosDTO deactivatedQuimico = quimicosService.deactivateQuimico(quimicoDTO);
            return ResponseEntity.ok(deactivatedQuimico);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error al eliminar el quimico: " + e.getMessage());
        }
    }

    @PutMapping("/activate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quimico activado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> activateQuimico(@RequestHeader("Authorization") String authHeader, @RequestBody QuimicosDTO quimicoDTO) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para activar frascos");
        }

        try{
            QuimicosDTO activatedQuimico = quimicosService.activateQuimico(quimicoDTO);
            return ResponseEntity.ok(activatedQuimico);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error al activar el quimico: " + e.getMessage());
        }    
    }

    @Operation(
        summary = "Añadir inventario a un quimico", 
        description = "Permite añadir inventario a un quimico existente en la base de datos. Solo necesita el numero a añadir y el nombre del quimico. \\n\\n" + //
                        "Si el quimico no existe, lanzará un error.")

    @PutMapping("/inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quimico reabastecido exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> restockQuimico(@RequestHeader("Authorization") String authHeader, @RequestBody QuimicosDTO quimicoDTO) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body("Este usuario no tiene permiso para reabastecer frascos");
        }

        try{
            QuimicosDTO restockedQuimico = quimicosService.changeInventory(quimicoDTO, authHeader.trim().replace("Bearer ", ""));
            return ResponseEntity.ok(restockedQuimico);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error al reabastecer el quimico: " + e.getMessage());
        }
    }
}