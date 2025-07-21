package com.sipcommb.envases.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.ExtractosDTO;
import com.sipcommb.envases.entity.Extractos;
import com.sipcommb.envases.repository.ExtractosRepository;

@Service
public class ExtractosService {

    @Autowired
    private ExtractosRepository extractosRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JwtService jwtService;
    
    // Method to add a new extracto
    public ExtractosDTO addExtracto(ExtractosDTO extractosDTO) {
        System.out.println("Adding new extracto: " + extractosDTO.getName());
        
        if (extractosDTO.getName() == null || extractosDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del extracto no puede estar vacío.");
        }

        Optional<Extractos> existingExtracto = extractosRepository.findByName(extractosDTO.getName().trim().toLowerCase());
        
        if (existingExtracto.isPresent()) {
            throw new IllegalArgumentException("Ya existe un extracto con el nombre: " + extractosDTO.getName());
        }

        if (extractosDTO.getPrice22ml() == null || extractosDTO.getPrice22ml() <= 0) {
            throw new IllegalArgumentException("El precio para 22ml no puede ser negativo o cero.");
        }

        if(extractosDTO.getQuantity() == null) {
            extractosDTO.setQuantity(0); // Default quantity if not provided
        }

        Extractos newExtracto = new Extractos();
        newExtracto.setName(extractosDTO.getName().trim().toLowerCase());
        newExtracto.setPrice22ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice22ml()));
        newExtracto.setPrice60ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice60ml()));
        newExtracto.setPrice125ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice125ml()));
        newExtracto.setPrice250ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice250ml()));
        newExtracto.setPrice500ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice500ml()));
        newExtracto.setPrice1000ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice1000ml()));
        newExtracto.setDescription(extractosDTO.getDescription() != null ? extractosDTO.getDescription() : "Sin descripción");
        newExtracto.setActive(true);
        newExtracto.setQuantity(extractosDTO.getQuantity());

        extractosRepository.save(newExtracto);

        return extractosDTO; // Replace with actual saved entity conversion
    }
    

    public List<ExtractosDTO> getAllExtractos() {
        return extractosRepository.findAll().stream()
                .map(ExtractosDTO::new)
                .collect(Collectors.toList());
    }

    public List<ExtractosDTO> getActiveExtractos() {
        return extractosRepository.findAllByActiveTrue().stream()
                .map(ExtractosDTO::new)
                .collect(Collectors.toList());
    }

    public List<ExtractosDTO> getInactiveExtractos() {
        return extractosRepository.findAllByActiveFalse().stream()
                .map(ExtractosDTO::new)
                .collect(Collectors.toList());
    }

    public ExtractosDTO updateExtracto(ExtractosDTO extractosDTO, String token) {
        
        if (extractosDTO.getName() == null || extractosDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del extracto no puede estar vacío.");
        }

        Optional<Extractos> existingExtracto = extractosRepository.findByName(extractosDTO.getName().trim().toLowerCase());
        
        if(!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + extractosDTO.getName());
        }

        Extractos extractoToUpdate = existingExtracto.get();

        if (extractosDTO.getDescription() != null) {
            extractoToUpdate.setDescription(extractosDTO.getDescription());
        }

        if (extractosDTO.getQuantity() != null) {
            extractoToUpdate.setQuantity(extractosDTO.getQuantity());
        }

        if (extractosDTO.getPrice22ml() != null && extractosDTO.getPrice22ml() > 0) {
            extractoToUpdate.setPrice22ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice22ml()));
        }else if(extractosDTO.getPrice22ml() != null && extractosDTO.getPrice22ml() < 0) {
            throw new IllegalArgumentException("El precio para 22ml no puede ser negativo o cero.");
        }

        if (extractosDTO.getPrice60ml() != null) {
            extractoToUpdate.setPrice60ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice60ml()));
        }

        if (extractosDTO.getPrice125ml() != null) {
            extractoToUpdate.setPrice125ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice125ml()));
        }

        if (extractosDTO.getPrice250ml() != null) {
            extractoToUpdate.setPrice250ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice250ml()));
        }

        if (extractosDTO.getPrice500ml() != null) {
            extractoToUpdate.setPrice500ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice500ml()));
        }

        if (extractosDTO.getPrice1000ml() != null) {
            extractoToUpdate.setPrice1000ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice1000ml()));
        }

        if(extractosDTO.getQuantity() != null && extractosDTO.getQuantity() != extractoToUpdate.getQuantity()) {
            extractoToUpdate.setQuantity(extractosDTO.getQuantity());
            inventoryService.newItem(
                extractoToUpdate.getId().longValue(),
                "extracto",
                extractosDTO.getQuantity().intValue(),
                "adjustment",
                jwtService.getUserIdFromToken(token).intValue(),
                "Se actualizo el inventario del extracto " + extractoToUpdate.getName()
            );
        }

        extractosRepository.save(extractoToUpdate);
        return new ExtractosDTO(extractoToUpdate);
    }

    public ExtractosDTO restock(ExtractosDTO extractosDTO, String token) {
        
        if (extractosDTO.getName() == null || extractosDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del extracto no puede estar vacío.");
        }

        Optional<Extractos> existingExtracto = extractosRepository.findByName(extractosDTO.getName().trim().toLowerCase());
        
        if(!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + extractosDTO.getName());
        }

        Extractos extractoToRestock = existingExtracto.get();
        extractoToRestock.setQuantity(extractoToRestock.getQuantity() + extractosDTO.getQuantity());

        inventoryService.newItem(
            extractoToRestock.getId().longValue(),
            "extracto",
            extractosDTO.getQuantity().intValue(),
            "restock",
            jwtService.getUserIdFromToken(token).intValue(),
            "Se reabasteció el inventario del extracto " + extractoToRestock.getName()
        );

        extractosRepository.save(extractoToRestock);
        return new ExtractosDTO(extractoToRestock);
    }
    
    public ExtractosDTO deactivateExtracto(String name) {
        Optional<Extractos> existingExtracto = extractosRepository.findByName(name.trim().toLowerCase());
        
        if(!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + name);
        }

        Extractos extractoToDeactivate = existingExtracto.get();
        extractoToDeactivate.setActive(false);
        extractosRepository.save(extractoToDeactivate);

        return new ExtractosDTO(extractoToDeactivate);
    }


    public ExtractosDTO activateExtracto(String name) {
        Optional<Extractos> existingExtracto = extractosRepository.findByName(name.trim().toLowerCase());
        
        if(!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + name);
        }

        Extractos extractoToActivate = existingExtracto.get();
        extractoToActivate.setActive(true);
        extractosRepository.save(extractoToActivate);

        return new ExtractosDTO(extractoToActivate);
    }

}
