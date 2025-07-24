package com.sipcommb.envases.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ExtractosDTO addExtracto(ExtractosDTO extractosDTO, String token) {
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

        inventoryService.newItem(newExtracto.getId() != null ? newExtracto.getId().longValue() : null, "extracto", newExtracto.getQuantity().intValue(), "add", jwtService.getUserIdFromToken(token).intValue(), "Se añadió " + newExtracto.getName() + " al inventario");

        extractosRepository.save(newExtracto);

        return extractosDTO; // Replace with actual saved entity conversion
    }
    

    public Page<ExtractosDTO> getAllExtractos(Pageable pageable) {
        return extractosRepository.findAll(pageable).map(ExtractosDTO::new);
    }

    public Page<ExtractosDTO> getActiveExtractos(Pageable pageable) {
        return extractosRepository.findAllByActiveTrue(pageable).map(ExtractosDTO::new);
    }

    public Page<ExtractosDTO> getInactiveExtractos(Pageable pageable) {
        return extractosRepository.findAllByActiveFalse(pageable).map(ExtractosDTO::new);
    }

    public Page<ExtractosDTO> getExtractosLikeName(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            return extractosRepository.findAll(pageable).map(ExtractosDTO::new);
        }
        return extractosRepository.findLikeName(name.trim(), pageable).map(ExtractosDTO::new);
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
                "Se actualizo el inventario del extracto " + extractoToUpdate.getName()+", su inventario ahora es: " + extractoToUpdate.getQuantity()
            );
        }

        extractosRepository.save(extractoToUpdate);
        return new ExtractosDTO(extractoToUpdate);
    }

    public ExtractosDTO changeInventory(ExtractosDTO extractosDTO, String token) {
        
        if (extractosDTO.getName() == null || extractosDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del extracto no puede estar vacío.");
        }

        Optional<Extractos> existingExtracto = extractosRepository.findByName(extractosDTO.getName().trim().toLowerCase());
        
        if(!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + extractosDTO.getName());
        }



        if(extractosDTO.getQuantity() == null || extractosDTO.getQuantity() == 0) {
            throw new IllegalArgumentException("La cantidad a reabastecer debe ser especificada.");
        }

         Extractos extractoToRestock = existingExtracto.get();

        if(extractosDTO.getQuantity() < 0) {
            extractoToRestock.setQuantity(extractoToRestock.getQuantity() + extractosDTO.getQuantity());
            inventoryService.newItem(
                extractoToRestock.getId().longValue(),
                "extracto",
                extractosDTO.getQuantity().intValue(),
                "damage",
                jwtService.getUserIdFromToken(token).intValue(),
                "Se ha reportado un daño en el extracto " + extractoToRestock.getName() + ", su inventario ahora es: " + extractoToRestock.getQuantity()
            );
            return new ExtractosDTO(extractosRepository.save(extractoToRestock));
        }

       
        extractoToRestock.setQuantity(extractoToRestock.getQuantity() + extractosDTO.getQuantity());

        inventoryService.newItem(
            extractoToRestock.getId().longValue(),
            "extracto",
            extractosDTO.getQuantity().intValue(),
            "restock",
            jwtService.getUserIdFromToken(token).intValue(),
            "hay " + extractosDTO.getQuantity() + " unidades nuevas de " + extractoToRestock.getName() + " en el inventario, en total hay " + extractoToRestock.getQuantity() + " unidades disponibles."
        );

        return new ExtractosDTO(extractosRepository.save(extractoToRestock));
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
