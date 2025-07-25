package com.sipcommb.envases.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sipcommb.envases.service.FileService;
import com.sipcommb.envases.service.PermissionService;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private FileService fileService;

    @PostMapping("/upload-excel")
    public ResponseEntity<?> uploadExcel(
        @RequestParam("file") MultipartFile file, 
        @RequestHeader("Authorization") String authHeader
    ) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No se ha subido ningun archivo");
        }
        
        

        try {

            if(!permissionService.hasPermission(authHeader, "create")) {
                return ResponseEntity.status(403).body("Este usuario no tiene permiso para crear tapas");
            }

            return ResponseEntity.ok(fileService.readFile(file, authHeader));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al subir el archivo: " + e.getMessage());
        }
    }
}
