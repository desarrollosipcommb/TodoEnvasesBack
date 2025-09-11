package com.sipcommb.envases.controller;


import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.CustomApiResponse;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.sipcommb.envases.dto.JarDTO;
import com.sipcommb.envases.dto.JarRequestDTO;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.dto.UpdateCompatibleCapsRequest;
import com.sipcommb.envases.service.JarService;
import com.sipcommb.envases.service.PermissionService;

@RestController
@RequestMapping("/jars")
@CrossOrigin(origins = "*")
public class JarController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JarService jarService;

    @PostMapping("/add")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Frasco creado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al crear el frasco")
    })
    public ResponseEntity<?> addJar(@RequestBody JarRequestDTO jarRequest, @RequestHeader("Authorization") String authHeader) {

        if(!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para crear frascos"));
        }

        try{
            JarDTO response = jarService.addJar(jarRequest, authHeader.replace("Bearer ", "").trim());
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }

    }

    @GetMapping("/all")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de frascos obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de frascos")
    })
    public ResponseEntity<?> getAllJars(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver frascos"));
        }

        try{
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok().body(jarService.getAllJars(pageable));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    
    @GetMapping("/all/active")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de frascos activos obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de frascos activos")
    })
    public ResponseEntity<?> getAllActiveJars(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver frascos"));
        }
        try{
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok().body(jarService.getAllActiveJars(pageable));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all/inactive")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de frascos inactivos obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de frascos inactivos")
    })
    public ResponseEntity<?> getAllInactiveJars(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver frascos"));
        }
        try{
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok().body(jarService.getAllInactiveJars(pageable));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Frasco actualizado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar el frasco")
    })
    public ResponseEntity<?> updateJar(@RequestBody JarRequestDTO jarRequest, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar frascos"));
        }
        try{
            JarDTO response = jarService.updateJar(jarRequest, authHeader.replace("Bearer ", "").trim());
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/update/compatible")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Frasco actualizado con capacidades compatibles exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar el frasco con capacidades compatibles")
    })
    public ResponseEntity<?> updateJarCompatibleCaps(@RequestBody UpdateCompatibleCapsRequest request, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar frascos"));
        }
        try {
            JarDTO response = jarService.updateCompatible(request.getCaps(), request.getName(),  request.isActive());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/delete")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Frasco eliminado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al eliminar el frasco")
    })
    public ResponseEntity<?> deleteJar(@RequestBody String jarName, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "delete")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para eliminar frascos"));
        }
        try {
            JarDTO response = jarService.deleteJar(jarName);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/activate")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Frasco activado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al activar el frasco")
    })
    public ResponseEntity<?> activateJar(@RequestBody String jarName, @RequestHeader("Authorization") String authHeader) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para activar frascos"));
        }
        try {
            JarDTO response = jarService.activateJar(jarName);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));

        }
    }

    @GetMapping("/by-name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Frasco obtenido exitosamente por nombre", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "404", description = "Frasco no encontrado")
    })
    public ResponseEntity<?> getJarByName(
        @RequestBody String name, 
        @RequestHeader("Authorization") String authHeader
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer frascos"));
        }
        try {
            JarDTO jarDTO = jarService.getJarByName(name);
            return ResponseEntity.ok(jarDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/like-name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Frascos obtenidos exitossamente por nombre", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "404", description = "Frascos no encontrados")
    })
    public ResponseEntity<?> getJarsByNameLike(
        @RequestParam String name,
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer frascos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(jarService.getJarLikeName(name, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomApiResponse(e.getMessage()));
        }
    }

    @PutMapping("/inventory")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cantidad del frasco actualizada exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al actualizar la cantidad del frasco")
    })
    public ResponseEntity<?> updateJarQuantity(@RequestBody JarRequestDTO jarRequestDTO, @RequestHeader("Authorization") String token) {
        if(!permissionService.hasPermission(token, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar frascos"));
        }
        try {
            JarDTO response = jarService.changeInventory(jarRequestDTO, token.replace("Bearer ", "").trim());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));   
        }
    }

    @GetMapping("/priceRange")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Frascos obtenidos exitosamente por rango de precios", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JarDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener los frascos por rango de precios")
    })
    public ResponseEntity<?> getJarsByPriceRange(
        @RequestHeader ("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestBody PriceSearchRequest priceSearchRequest
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer frascos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(jarService.getPriceRange(priceSearchRequest, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error: " + e.getMessage()));
        }
    }

  @GetMapping("/by-name-diameter")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Envases obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CapDTO.class))),
      @ApiResponse(responseCode = "403", description = "Permiso denegado"),
      @ApiResponse(responseCode = "404", description = "Tapa no encontrada")
  })
  public ResponseEntity<?> getCapByName(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String diameter,
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    if(!permissionService.hasPermission(authHeader, "read")) {
      return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para leer las tapas"));
    }
    try {
      Pageable pageable = PageRequest.of(page, size);
      return ResponseEntity.ok(jarService.getFromNameLikeAndNameDiameter(name,diameter, pageable));
    } catch (Exception e) {
      return ResponseEntity.status(404).body(new CustomApiResponse(e.getMessage()));
    }
  }

}
