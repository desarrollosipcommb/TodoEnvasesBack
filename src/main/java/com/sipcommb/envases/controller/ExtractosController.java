package com.sipcommb.envases.controller;


import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.dto.ExtractosDTO;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.service.ExtractosService;
import com.sipcommb.envases.service.PermissionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/extractos")
@CrossOrigin(origins = "*")
public class ExtractosController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ExtractosService extractosService;

    @PostMapping("/add")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extracto agregado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> addExtracto(@RequestHeader("Authorization") String authHeader, @RequestBody ExtractosDTO extractosDTO) {
        if(!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para crear extractos"));
        }

        try {
            ExtractosDTO newExtracto = extractosService.addExtracto(extractosDTO, authHeader);
            return ResponseEntity.ok(newExtracto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de extractos obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllExtractos(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver extractos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(extractosService.getAllExtractos(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al obtener los extractos: " + e.getMessage()));
        }
    }

    @GetMapping("/all/active")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extractos activos obtenidos exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllActiveExtractos(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver extractos activos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(extractosService.getActiveExtractos(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al obtener los extractos activos: " + e.getMessage()));
        }   
    }

    @GetMapping("/all/inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extractos inactivos obtenidos exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllInactiveExtractos(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver extractos inactivos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(extractosService.getInactiveExtractos(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al obtener los extractos inactivos: " + e.getMessage()));
        }
    }

    @GetMapping("/like-name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extractos obtenidos exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener los extractos")
    })
    public ResponseEntity<?> getExtractosLikeName(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver extractos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(extractosService.getExtractosLikeName(name, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al obtener los extractos: " + e.getMessage()));
        }
    }

    @GetMapping("/like-name/active")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extractos obtenidos exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "403", description = "Permiso denegado"),
        @ApiResponse(responseCode = "400", description = "Error al obtener los extractos")
    })
    public ResponseEntity<?> getExtractosLikeNameActive(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if(!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver extractos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(extractosService.getExtractosLikeNameActive(name, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al obtener los extractos: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extracto actualizado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> updateExtracto(@RequestHeader("Authorization") String authHeader, @RequestBody ExtractosDTO extractosDTO) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para actualizar extractos"));
        }
        try {
            ExtractosDTO updatedExtracto = extractosService.updateExtracto(extractosDTO, authHeader);
            return ResponseEntity.ok(updatedExtracto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al actualizar el extracto: " + e.getMessage()));
        }
    }

    @PutMapping("/delete")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extracto eliminado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> deleteExtracto(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody ExtractosDTO extractosDTO) 
        {
        if(!permissionService.hasPermission(authHeader, "delete")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para eliminar extractos"));
        }
        try {
            ExtractosDTO response = extractosService.deactivateExtracto(extractosDTO.getName().trim().toLowerCase());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al eliminar el extracto: " + e.getMessage()));
        }
    }

    @PutMapping("/activate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extracto activado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> activateExtracto(@RequestHeader("Authorization") String authHeader, @RequestBody ExtractosDTO extractosDTO) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para activar extractos"));
        }
        try {
            ExtractosDTO response = extractosService.activateExtracto(extractosDTO.getName().trim().toLowerCase());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al activar el extracto: " + e.getMessage()));
        }
    }

    @PutMapping("/inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extracto actualizado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> restockExtracto(@RequestHeader("Authorization") String authHeader, @RequestBody ExtractosDTO extractosDTO) {
        if(!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para reabastecer extractos"));
        }
        try {
            ExtractosDTO restockedExtracto = extractosService.changeInventory(extractosDTO, authHeader.trim().replace("Bearer ", ""));
            return ResponseEntity.ok(restockedExtracto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al reabastecer el extracto: " + e.getMessage()));
        }
    }

    @GetMapping("/priceRange")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extractos por rango de precio obtenidos exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExtractosDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getPriceRange(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody PriceSearchRequest priceSearchRequest,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        
        try {
            if(!permissionService.hasPermission(authHeader, "read")) {
                return ResponseEntity.status(403).body(new CustomApiResponse("Este usuario no tiene permiso para ver extractos"));
            }   

            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(extractosService.getPriceRange(priceSearchRequest, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse("Error al obtener los extractos por rango de precio: " + e.getMessage()));
        }
    }
}
