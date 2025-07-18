package com.sipcommb.envases.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.QuimicosDTO;
import com.sipcommb.envases.entity.Quimicos;
import com.sipcommb.envases.repository.QuimicosRepository;

@Service
public class QuimicosService {

    @Autowired
    private QuimicosRepository quimicosRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JwtService jwtService;

    public QuimicosDTO addQuimico(QuimicosDTO quimicoDTO) {

        if(quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

       if(quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        if(quimicoDTO.getUnitPrice() == null || quimicoDTO.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        if(quimicoDTO.getQuantity() == null){
            quimicoDTO.setQuantity(0); // Default quantity if not provided
        }

        if(quimicoDTO.getDescription().isEmpty()){
            quimicoDTO.setDescription("Sin descripción"); // Default description if not provided
        }

        Quimicos quimico = new Quimicos();
        quimico.setName(quimicoDTO.getName().trim().toLowerCase());
        quimico.setDescription(quimicoDTO.getDescription());
        quimico.setQuantity(quimicoDTO.getQuantity());
        quimico.setUnitPrice(BigDecimal.valueOf(quimicoDTO.getUnitPrice()));


        quimicosRepository.save(quimico);

        return quimicoDTO;
    }

    public List<QuimicosDTO> getAllQuimicos() {
        List<Quimicos> quimicosList = quimicosRepository.findAll();
        return quimicosList.stream().map(QuimicosDTO::new).collect(Collectors.toList());
    }

    public List<QuimicosDTO> getActiveQuimicos() {
        List<Quimicos> quimicosList = quimicosRepository.findByActiveTrue();
        return quimicosList.stream().map(QuimicosDTO::new).collect(Collectors.toList());
    }

    public List<QuimicosDTO> getInactiveQuimicos() {
        List<Quimicos> quimicosList = quimicosRepository.findByActiveFalse();
        return quimicosList.stream().map(QuimicosDTO::new).collect(Collectors.toList());
    }

    public QuimicosDTO updateQuimico(QuimicosDTO quimicoDTO, String token) {

        if(quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }
        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if(!quimicoOpt.isPresent()) {
           throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();
       
        if(quimicoDTO.getDescription() != null) {
            quimico.setDescription(quimicoDTO.getDescription());
        }

        if(quimicoDTO.getQuantity() != null) {
            quimico.setQuantity(quimicoDTO.getQuantity());
            inventoryService.newItem(quimico.getId().longValue(), "quimico", quimico.getQuantity().intValue(), "adjustment", jwtService.getUserIdFromToken(token).intValue(), "Se actualizo el inventario del quimico " + quimico.getName());
        }

        if(quimicoDTO.getUnitPrice() != null && quimicoDTO.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        if(quimicoDTO.getUnitPrice() != null && quimicoDTO.getUnitPrice() > 0) {
            quimico.setUnitPrice(BigDecimal.valueOf(quimicoDTO.getUnitPrice()));
        }

        quimicosRepository.save(quimico);

        return new QuimicosDTO(quimico);
    }

    public QuimicosDTO restock(QuimicosDTO quimicoDTO, String token) {
        if(quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if(!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();

        if(quimicoDTO.getQuantity() == null || quimicoDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad a reabastecer debe ser mayor que cero.");
        }

        quimico.setQuantity(quimico.getQuantity() + quimicoDTO.getQuantity());

        inventoryService.newItem(
            quimico.getId().longValue(),
            "quimico",
            quimicoDTO.getQuantity(),
            "restock",
            jwtService.getUserIdFromToken(token).intValue(),
            "Se reabasteció el inventario del quimico " + quimico.getName()
        );

        quimicosRepository.save(quimico);
        return new QuimicosDTO(quimico);
    }

    public QuimicosDTO activateQuimico(QuimicosDTO quimicoDTO) {
        if(quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if(!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();
        quimico.setActive(true); // Activar el quimico
        quimicosRepository.save(quimico);
        return new QuimicosDTO(quimico);
    }

    public QuimicosDTO deactivateQuimico(QuimicosDTO quimicoDTO) {
        if(quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if(!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();
        quimico.setActive(false); // Desactivar el quimico
        quimicosRepository.save(quimico);
        return new QuimicosDTO(quimico);
    }


}