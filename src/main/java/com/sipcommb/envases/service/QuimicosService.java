package com.sipcommb.envases.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.PriceSearchRequest;
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

    @Autowired
    private PriceService priceService;

    public QuimicosDTO addQuimico(QuimicosDTO quimicoDTO, String token) {

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
        inventoryService.newItem(quimico.getId().longValue(), "quimico", quimico.getQuantity().intValue(), "restock", jwtService.getUserIdFromToken(token).intValue(), "Se creo el quimico " + quimico.getName());

        return quimicoDTO;
    }

    public Page<QuimicosDTO> getAllQuimicos(Pageable pageable) {
        Page<Quimicos> quimicosPage = quimicosRepository.findAll(pageable);
        return quimicosPage.map(QuimicosDTO::new);
    }

    public Page<QuimicosDTO> getActiveQuimicos(Pageable pageable) {
        Page<Quimicos> quimicosPage = quimicosRepository.findByActiveTrue(pageable);
        return quimicosPage.map(QuimicosDTO::new);
    }

    public Page<QuimicosDTO> getInactiveQuimicos(Pageable pageable) {
        Page<Quimicos> quimicosPage = quimicosRepository.findByActiveFalse(pageable);
        return quimicosPage.map(QuimicosDTO::new);
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

        if(quimicoDTO.getQuantity() != null && quimicoDTO.getQuantity() != quimico.getQuantity()) {
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

    public QuimicosDTO changeInventory(QuimicosDTO quimicoDTO, String token) {
        if(quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if(!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();

        if(quimicoDTO.getQuantity() == null) {
            throw new IllegalArgumentException("La cantidad a reabastecer debe ser especificada.");
        }

        if(quimicoDTO.getQuantity() < 0) {
            quimico.setQuantity(quimico.getQuantity() + quimicoDTO.getQuantity());
            inventoryService.newItem(
                quimico.getId().longValue(),
                "quimico",
                quimicoDTO.getQuantity(),
                "damage",
                jwtService.getUserIdFromToken(token).intValue(),
                "Se reportó un daño en el inventario del quimico " + quimico.getName() + ", su inventario ahora es: " + quimico.getQuantity()
            );
            return new QuimicosDTO(quimicosRepository.save(quimico));
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
        return new QuimicosDTO(quimicosRepository.save(quimico));
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

    public Page<QuimicosDTO> getPriceRange(PriceSearchRequest priceSearchRequest, Pageable pageable) {
       boolean exactSearch =priceService.verifyPriceSearchRequest(priceSearchRequest);

       if(exactSearch) {
            return quimicosRepository.findByExactPrice(priceSearchRequest.getExactPrice(), pageable).map(QuimicosDTO::new);
        } else {
            return quimicosRepository.findByPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(QuimicosDTO::new);
        }
    

    }


}