package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.BodegaMovementDTO;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.dto.QuimicoRequestDTO;
import com.sipcommb.envases.dto.QuimicosDTO;
import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.entity.BodegaQuimicos;
import com.sipcommb.envases.entity.Quimicos;
import com.sipcommb.envases.repository.BodegaQuimicoRepository;
import com.sipcommb.envases.repository.QuimicosRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Autowired
    private BodegaService bodegaService;

    @Autowired
    private BodegaQuimicoRepository bodegaQuimicoRepository;

    public QuimicosDTO addQuimico(QuimicoRequestDTO quimicoDTO, String token) {

        if (quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

        if (quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        if (quimicoDTO.getUnitPrice() == null || quimicoDTO.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        Bodega bodega = bodegaService.getBodegaByName(quimicoDTO.getBodegaName());

        if (quimicoDTO.getQuantity() == null) {
            quimicoDTO.setQuantity(0); // Default quantity if not provided
        }

        if (quimicoDTO.getDescription().isEmpty()) {
            quimicoDTO.setDescription("Sin descripción"); // Default description if not provided
        }

        Quimicos quimico = new Quimicos();
        quimico.setName(quimicoDTO.getName().trim().toLowerCase());
        quimico.setDescription(quimicoDTO.getDescription());
        quimico.setUnitPrice(BigDecimal.valueOf(quimicoDTO.getUnitPrice()));

        quimicosRepository.save(quimico);

        bodegaQuimicoRepository.save(new BodegaQuimicos(bodega, quimico, quimicoDTO.getQuantity()));

        quimicosRepository.save(quimico);

        inventoryService.newItem(quimico.getId().longValue(), "quimico", quimicoDTO.getQuantity().intValue(), "restock", jwtService.getUserIdFromToken(token).intValue(), "Se creo el quimico " + quimico.getName());

        return new QuimicosDTO(quimico);
    }

    public Page<QuimicosDTO> getAllQuimicos(Pageable pageable) {
        Page<Quimicos> quimicosPage = quimicosRepository.findAll(pageable);
        return quimicosPage.map(QuimicosDTO::new);
    }

    public Page<QuimicosDTO> getAllQuimicosByName(String searchName, Pageable pageable) {
        Page<Quimicos> quimicosPage = quimicosRepository.findByNameContainingIgnoreCase(searchName, pageable);
        return quimicosPage.map(QuimicosDTO::new);
    }

    public Page<QuimicosDTO> getAllQuimicosByNameActive(String searchName, Pageable pageable) {
        Page<Quimicos> quimicosPage = quimicosRepository.findByNameContainingIgnoreCaseAndActiveTrue(searchName, pageable);
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

    public QuimicosDTO updateQuimico(QuimicoRequestDTO quimicoDTO, String token) {

        if (quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }
        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if (!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();

        Bodega bodega = bodegaService.getBodegaByName(quimicoDTO.getBodegaName());

        Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega, quimico);

        if(!bodegaQuimicosOpt.isPresent()) {
            throw new IllegalArgumentException("El quimico " + quimico.getName() + " no está asociado a la bodega " + bodega.getName());
        }

        BodegaQuimicos bodegaQuimicos = bodegaQuimicosOpt.get();

        if (quimicoDTO.getDescription() != null) {
            quimico.setDescription(quimicoDTO.getDescription());
        }

        if (quimicoDTO.getQuantity() != null && quimicoDTO.getQuantity() != bodegaQuimicos.getQuantity()) {
            bodegaQuimicos.setQuantity(quimicoDTO.getQuantity());
            bodegaQuimicoRepository.save(bodegaQuimicos);
            inventoryService.newItem(quimico.getId().longValue(), "quimico", bodegaQuimicos.getQuantity().intValue(), "adjustment", jwtService.getUserIdFromToken(token).intValue(), "Se actualizo el inventario del quimico " + quimico.getName());
        }

        if (quimicoDTO.getUnitPrice() != null && quimicoDTO.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        if (quimicoDTO.getUnitPrice() != null && quimicoDTO.getUnitPrice() > 0) {
            quimico.setUnitPrice(BigDecimal.valueOf(quimicoDTO.getUnitPrice()));
        }

        quimicosRepository.save(quimico);

        return new QuimicosDTO(quimico);
    }

     
    public QuimicosDTO updateInventoryQuimico(String nameJar, Integer quantityQuimico,  String bodegaName, String token) {
        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(nameJar.trim().toLowerCase());

        if (!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un químico con ese nombre.");
        }
        
        Quimicos quimico = quimicoOpt.get();

        Bodega bodega = bodegaService.getBodegaByName(bodegaName);

        Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega, quimico);

        if(!bodegaQuimicosOpt.isPresent()) {
            throw new IllegalArgumentException("El quimico " + quimico.getName() + " no está asociado a la bodega " + bodega.getName());
        }

        BodegaQuimicos bodegaQuimicos = bodegaQuimicosOpt.get();

        if (quantityQuimico == null) {
            throw new IllegalArgumentException("La cantidad no puede ser nula.");
        }

        if (quantityQuimico < 0) {
            bodegaQuimicos.setQuantity(Math.max((bodegaQuimicos.getQuantity() + quantityQuimico), 0));
            bodegaQuimicoRepository.save(bodegaQuimicos);
            inventoryService.newItem(quimico.getId().longValue(), "quimico", bodegaQuimicos.getQuantity(),
                    "damage", jwtService.getUserIdFromToken(token).intValue(), "Se reporto un daño en "
                            + quimico.getName() + " su inventario ahora es: " + bodegaQuimicos.getQuantity());
            return new QuimicosDTO(quimicosRepository.save(quimico));
        }

        bodegaQuimicos.setQuantity(quantityQuimico);
        bodegaQuimicoRepository.save(bodegaQuimicos);
        inventoryService.newItem(quimico.getId().longValue(), "quimico", bodegaQuimicos.getQuantity(), "restock",
                jwtService.getUserIdFromToken(token).intValue(), "Se actualizo " + quimico.getName()
                        + " su inventario ahora es: " + bodegaQuimicos.getQuantity());

        return new QuimicosDTO(quimicosRepository.save(quimico));
    }
        

    public QuimicosDTO changeInventory(QuimicoRequestDTO quimicoDTO, String token) {
        if (quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if (!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();

        Bodega bodega = bodegaService.getBodegaByName(quimicoDTO.getBodegaName());

        Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega, quimico);


        if (quimicoDTO.getQuantity() == null) {
            throw new IllegalArgumentException("La cantidad a reabastecer debe ser especificada.");
        }


        BodegaQuimicos bodegaQuimicos = null;
        if(!bodegaQuimicosOpt.isPresent()) {
            bodegaQuimicos = new BodegaQuimicos(bodega, quimico, 0);
        }else {
            bodegaQuimicos = bodegaQuimicosOpt.get();
        }

        if (quimicoDTO.getQuantity() < 0) {
            bodegaQuimicos.setQuantity(bodegaQuimicos.getQuantity() + quimicoDTO.getQuantity());
            bodegaQuimicoRepository.save(bodegaQuimicos);
            inventoryService.newItem(
                    quimico.getId().longValue(),
                    "quimico",
                    quimicoDTO.getQuantity(),
                    "damage",
                    jwtService.getUserIdFromToken(token).intValue(),
                    "Se reportó un daño en el inventario del quimico " + quimico.getName() + ", su inventario ahora es: " + bodegaQuimicos.getQuantity()
            );
            return new QuimicosDTO(quimicosRepository.save(quimico));
        }

        bodegaQuimicos.setQuantity(bodegaQuimicos.getQuantity() + quimicoDTO.getQuantity());
        bodegaQuimicoRepository.save(bodegaQuimicos);
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
        if (quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if (!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();
        quimico.setActive(true); // Activar el quimico
        quimicosRepository.save(quimico);
        return new QuimicosDTO(quimico);
    }

    public QuimicosDTO deactivateQuimico(QuimicosDTO quimicoDTO) {
        if (quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if (!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();
        quimico.setActive(false); // Desactivar el quimico
        quimicosRepository.save(quimico);
        return new QuimicosDTO(quimico);
    }

    public Page<QuimicosDTO> getPriceRange(PriceSearchRequest priceSearchRequest, Pageable pageable) {
        boolean exactSearch = priceService.verifyPriceSearchRequest(priceSearchRequest);

        if (exactSearch) {
            return quimicosRepository.findByExactPrice(priceSearchRequest.getExactPrice(), pageable).map(QuimicosDTO::new);
        } else {
            return quimicosRepository.findByPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(QuimicosDTO::new);
        }


    }

    public QuimicosDTO addBodega(QuimicoRequestDTO quimicoDTO) {
        if (quimicoDTO.getName() == null || quimicoDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del quimico no puede estar vacío.");
        }

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(quimicoDTO.getName().trim().toLowerCase());

        if (!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un quimico con el nombre: " + quimicoDTO.getName());
        }

        Quimicos quimico = quimicoOpt.get();

        Bodega bodega = bodegaService.getBodegaByName(quimicoDTO.getBodegaName());

        Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega, quimico);

        if(bodegaQuimicosOpt.isPresent()) {
            throw new IllegalArgumentException("El quimico " + quimico.getName() + " ya está asociado a la bodega " + bodega.getName());
        }

        BodegaQuimicos bodegaQuimicos = new BodegaQuimicos(bodega, quimico, quimicoDTO.getQuantity() != null ? quimicoDTO.getQuantity() : 0);
        bodegaQuimicoRepository.save(bodegaQuimicos);

        return new QuimicosDTO(quimico);

    }

    public QuimicosDTO bodegaTranfer(BodegaMovementDTO request) {
        Quimicos quimico = quimicosRepository.findByName(request.getItemName().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("No existe un quimico con el nombre: " + request.getItemName()));
        
        if(request.getBodegaFrom().equalsIgnoreCase(request.getBodegaTo())){
            throw new IllegalArgumentException("La bodega de origen y destino no pueden ser la misma.");
        }

        Bodega bodegaFrom = bodegaService.getBodegaByName(request.getBodegaFrom());
        Bodega bodegaTo = bodegaService.getBodegaByName(request.getBodegaTo());

        BodegaQuimicos bqFrom = bodegaQuimicoRepository.findByBodegaAndQuimico(bodegaFrom, quimico)
                .orElseThrow(() -> new IllegalArgumentException("El quimico " + quimico.getName() + " no está asociado a la bodega de origen " + request.getBodegaFrom()));

        BodegaQuimicos bqTo = bodegaQuimicoRepository.findByBodegaAndQuimico(bodegaTo, quimico)
                .orElseThrow(() -> new IllegalArgumentException("El quimico " + quimico.getName() + " no está asociado a la bodega de destino " + request.getBodegaTo()));

        if(bodegaFrom == null){
            throw new IllegalArgumentException("El quimico " + quimico.getName() + " no está asociado a la bodega de origen " + request.getBodegaFrom());
        }

        if(bodegaTo == null){
            throw new IllegalArgumentException("El quimico " + quimico.getName() + " no está asociado a la bodega de destino " + request.getBodegaTo());
        }

        if(bqFrom.getQuantity() < request.getQuantity()){
            throw new IllegalArgumentException("No hay suficiente inventario en la bodega de origen. Inventario actual: " + bqFrom.getQuantity());
        }

        bqFrom.setQuantity(bqFrom.getQuantity() - request.getQuantity());
        bqTo.setQuantity(bqTo.getQuantity() + request.getQuantity());

        bodegaQuimicoRepository.save(bqFrom);
        bodegaQuimicoRepository.save(bqTo);

        return new QuimicosDTO(quimico);
    }

}