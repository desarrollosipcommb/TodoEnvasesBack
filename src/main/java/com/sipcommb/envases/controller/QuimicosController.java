package com.sipcommb.envases.controller;

import com.sipcommb.envases.dto.BodegaMovementDTO;
import com.sipcommb.envases.dto.CustomApiResponse;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.dto.QuimicoRequestDTO;
import com.sipcommb.envases.dto.QuimicosDTO;
import com.sipcommb.envases.service.PermissionService;
import com.sipcommb.envases.service.QuimicosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<?> addQuimico(@RequestHeader("Authorization") String authHeader,
            @RequestBody QuimicoRequestDTO quimicoDTO) {
        if (!permissionService.hasPermission(authHeader, "create")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para crear frascos"));
        }

        try {
            QuimicosDTO newQuimico = quimicosService.addQuimico(quimicoDTO, authHeader.trim().replace("Bearer ", ""));
            return ResponseEntity.ok(newQuimico);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al agregar el quimico: " + e.getMessage()));
        }

    }

    @GetMapping("/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de quimicos obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllQuimicos(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver frascos"));
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(quimicosService.getAllQuimicos(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al obtener los quimicos: " + e.getMessage()));
        }
    }

    @GetMapping("/all/active")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quimicos activos obtenidos exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllActiveQuimicos(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver frascos"));
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(quimicosService.getActiveQuimicos(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al obtener los quimicos activos: " + e.getMessage()));
        }
    }

    @GetMapping("/all/inactive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quimicos inactivos obtenidos exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> getAllInactiveQuimicos(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver frascos"));
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(quimicosService.getInactiveQuimicos(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al obtener los quimicos inactivos: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quimico actualizado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> updateQuimico(@RequestHeader("Authorization") String authHeader,
            @RequestBody QuimicoRequestDTO quimicoDTO) {
        if (!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para actualizar frascos"));
        }

        try {
            QuimicosDTO updatedQuimico = quimicosService.updateQuimico(quimicoDTO,
                    permissionService.getToken(authHeader));
            return ResponseEntity.ok(updatedQuimico);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al actualizar el quimico: " + e.getMessage()));
        }
    }

    @PutMapping("/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quimico eliminado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> deleteQuimico(@RequestHeader("Authorization") String authHeader,
            @RequestBody QuimicosDTO quimicoDTO) {
        if (!permissionService.hasPermission(authHeader, "delete")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para eliminar frascos"));
        }

        try {
            QuimicosDTO deactivatedQuimico = quimicosService.deactivateQuimico(quimicoDTO);
            return ResponseEntity.ok(deactivatedQuimico);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al eliminar el quimico: " + e.getMessage()));
        }
    }

    @PutMapping("/activate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quimico activado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> activateQuimico(@RequestHeader("Authorization") String authHeader,
            @RequestBody QuimicosDTO quimicoDTO) {
        if (!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para activar frascos"));
        }

        try {
            QuimicosDTO activatedQuimico = quimicosService.activateQuimico(quimicoDTO);
            return ResponseEntity.ok(activatedQuimico);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al activar el quimico: " + e.getMessage()));
        }
    }

    @Operation(summary = "Añadir inventario a un quimico", description = "Permite añadir inventario a un quimico existente en la base de datos. Solo necesita el numero a añadir y el nombre del quimico. \\n\\n"
            + //
            "Si el quimico no existe, lanzará un error.")

    @PutMapping("/inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quimico reabastecido exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> restockQuimico(@RequestHeader("Authorization") String authHeader,
            @RequestBody QuimicoRequestDTO quimicoDTO) {
        if (!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para reabastecer frascos"));
        }

        try {
            QuimicosDTO restockedQuimico = quimicosService.changeInventory(quimicoDTO,
                    authHeader.trim().replace("Bearer ", ""));
            return ResponseEntity.ok(restockedQuimico);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al reabastecer el quimico: " + e.getMessage()));
        }
    }

    @GetMapping("/priceRange")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rango de precios obtenido exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    public ResponseEntity<?> getPriceRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PriceSearchRequest priceSearchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver frascos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(quimicosService.getPriceRange(priceSearchRequest, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al obtener el rango de precios: " + e.getMessage()));
        }
    }

    @GetMapping("/byName")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de quimicos", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    public ResponseEntity<?> getByName(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(name = "searchName", required = false, defaultValue = "") String searchName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver frascos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
            return ResponseEntity.ok(quimicosService.getAllQuimicosByName(searchName, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al obtener el rango de precios: " + e.getMessage()));
        }
    }

    @GetMapping("/byName/active")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de quimicos", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "403", description = "Permiso denegado"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    public ResponseEntity<?> getByNameActive(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(name = "searchName", required = false, defaultValue = "") String searchName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!permissionService.hasPermission(authHeader, "read")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para ver frascos"));
        }
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
            return ResponseEntity.ok(quimicosService.getAllQuimicosByNameActive(searchName, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al obtener el rango de precios: " + e.getMessage()));
        }
    }

    @PutMapping("/add/bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quimico asociado a bodega exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> addQuimicoToBodega(@RequestHeader("Authorization") String authHeader,
            @RequestBody QuimicoRequestDTO quimicoDTO) {
        if (!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para asociar quimicos a bodegas"));
        }
        try {
            QuimicosDTO updatedQuimico = quimicosService.addBodega(quimicoDTO);
            return ResponseEntity.ok(updatedQuimico);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al asociar el quimico a la bodega: " + e.getMessage()));
        }
    }

    @PutMapping("bodega_transfer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferencia de bodega exitosa", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = QuimicosDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    public ResponseEntity<?> bodegaTransfer(@RequestHeader("Authorization") String authHeader,
            @RequestBody BodegaMovementDTO request) {
        if (!permissionService.hasPermission(authHeader, "update")) {
            return ResponseEntity.status(403)
                    .body(new CustomApiResponse("Este usuario no tiene permiso para transferir inventario entre bodegas"));
        }
        try {
            QuimicosDTO result = quimicosService.bodegaTranfer(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new CustomApiResponse("Error al transferir inventario entre bodegas: " + e.getMessage()));
        }   
    }
    
}