package com.sipcommb.envases.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

import com.sipcommb.envases.dto.ClientDTO;
import com.sipcommb.envases.dto.ClientRequestDTO;
import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.service.ClientService;
import com.sipcommb.envases.service.PermissionService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/clients")
@CrossOrigin(origins = "*")
public class ClientController {
    
    @Autowired
    private ClientService clientService;

    @Autowired 
    private PermissionService permissionService;

    @PostMapping("/add")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente añadido correctamente", content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al crear la tapa")
    })
    public ResponseEntity<?> addClient(
        @RequestBody ClientDTO request,
        @RequestHeader("Authorization") String authHeader
    ){
        if (!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para crear tapas"));
        }
        try{
            ClientDTO clientDTO = clientService.addClient(request);
            return ResponseEntity.ok(clientDTO);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: "+ e.getMessage()));
        }
    }

    @GetMapping("/all")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Se retornaron los clientes correctamente", content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de clientes")
    })
    public ResponseEntity<?> getAllClients(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas"));
        }

        try{
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(clientService.getAllClients(pageable));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: "+ e.getMessage()));
        }
    }

    @PutMapping("/update")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Se actualizo el cliente correctamente", content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar el cliente")
    })
    public ResponseEntity<?> updateClient(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody ClientRequestDTO request
    ){
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para actualizar tapas"));
        }
        try{
            return ResponseEntity.ok(clientService.updateClient(request));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: ", e.getMessage()));
        }
    }

    @GetMapping("/all/active")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Se retornaron los clientes activos correctamente", content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener los clientes")
    })
    public ResponseEntity<?> getAllActive(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas"));
        }
        try{
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(clientService.getAllClientsActive(pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: "+ e.getMessage()));
        }
    }

    @GetMapping("/all/inActive")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Se retornaron los clientes in-actives correctamente", content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener los clientes")
    })
    public ResponseEntity<?> getAllInActive(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas"));
        }
        try{
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(clientService.getAllClientsInActive(pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: "+ e.getMessage()));
        }
    }

    @GetMapping("/all/likeName")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Se retornaron los clientes con el nombre especificado", content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener los clientes")
    })
    public ResponseEntity<?> getLikeName(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String name
    ){
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver las tapas"));
        }
        try{
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(clientService.getClientsLikeName(pageable, name));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: "+ e.getMessage()));
        }
    }

    @PutMapping("/activate")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Se cambio el estado del cliente correctamente", content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al cambiar el estado del cliente")
    })
    public ResponseEntity<?> changeState(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String name
    ){
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para actualizar tapas"));
        }
        try{
            return ResponseEntity.ok(clientService.changeState(name, true));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: ", e.getMessage()));
        }
    }

    @PutMapping("/deactivate")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Se cambio el estado del cliente correctamente", content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al cambiar el estado del cliente")
    })
    public ResponseEntity<?> deactivateClient(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String name
    ){
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para actualizar tapas"));
        }
        try{
            return ResponseEntity.ok(clientService.changeState(name, false));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: ", e.getMessage()));
        }
    }

}
