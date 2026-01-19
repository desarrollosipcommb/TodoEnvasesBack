package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.BodegaDTO;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        for (BodegaDTO bodegaDTO : quimicoDTO.getBodegaName()) {
            if (bodegaDTO.getName() == null || bodegaDTO.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la bodega no puede estar vacío.");
            }
            if (bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad en la bodega " + bodegaDTO.getName() + " nula.");
            }
        }

        if (quimicoDTO.getDescription().isEmpty()) {
            quimicoDTO.setDescription("Sin descripción"); // Default description if not provided
        }

        Quimicos quimico = new Quimicos();
        quimico.setName(quimicoDTO.getName().trim().toLowerCase());
        quimico.setDescription(quimicoDTO.getDescription());
        quimico.setUnitPrice(BigDecimal.valueOf(quimicoDTO.getUnitPrice()));

        quimicosRepository.save(quimico);
        List<BodegaQuimicos> bodegaQuimicosList = new ArrayList<>();
        for (BodegaDTO bodegaDTO : quimicoDTO.getBodegaName()) {
            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            BodegaQuimicos bodegaQuimicos = new BodegaQuimicos(bodega, quimico, bodegaDTO.getQuantity());
            bodegaQuimicosList.add(bodegaQuimicos);

            inventoryService.newItem(
                    quimico.getId().longValue(),
                    "quimico",
                    bodegaDTO.getQuantity().intValue(),
                    "restock",
                    jwtService.getUserIdFromToken(token).intValue(),
                    "Se creo el quimico " + quimico.getName());
        }
        batchSaveBodegaQuimicos(bodegaQuimicosList);
        quimicosRepository.save(quimico);

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
        Page<Quimicos> quimicosPage = quimicosRepository.findByNameContainingIgnoreCaseAndActiveTrue(searchName,
                pageable);
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

        if (quimicoDTO.getDescription() != null) {
            quimico.setDescription(quimicoDTO.getDescription());
        }

        if (quimicoDTO.getUnitPrice() != null && quimicoDTO.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        if (quimicoDTO.getUnitPrice() != null && quimicoDTO.getUnitPrice() > 0) {
            quimico.setUnitPrice(BigDecimal.valueOf(quimicoDTO.getUnitPrice()));
        }

        for (BodegaDTO bodegaDTO : quimicoDTO.getBodegaName()) {
            if (bodegaDTO.getName() == null || bodegaDTO.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la bodega no puede estar vacío.");
            }
            if (bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad en la bodega " + bodegaDTO.getName() + " nula.");
            }

            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega,
                    quimico);

            if (!bodegaQuimicosOpt.isPresent()) {
                throw new IllegalArgumentException(
                        "El quimico " + quimico.getName() + " no está asociado a la bodega " + bodega.getName());
            }
        }

        for (BodegaDTO bodegaDTO : quimicoDTO.getBodegaName()) {
            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega,
                    quimico);
            BodegaQuimicos bodegaQuimicos = bodegaQuimicosOpt.get();

            if (bodegaDTO.getQuantity() != null && bodegaDTO.getQuantity() != bodegaQuimicos.getQuantity()) {
                bodegaQuimicos.setQuantity(bodegaDTO.getQuantity());
                bodegaQuimicoRepository.save(bodegaQuimicos);
                inventoryService.newItem(
                        quimico.getId().longValue(),
                        "quimico",
                        bodegaQuimicos.getQuantity().intValue(),
                        "adjustment",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se actualizo el inventario del quimico " + quimico.getName());
            }
        }

        quimicosRepository.save(quimico);

        return new QuimicosDTO(quimico);

    }

    public QuimicosDTO updateInventoryQuimico(String nameJar, List<BodegaDTO> bodegaDTOs, String token) {

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(nameJar.trim().toLowerCase());

        if (!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un químico con ese nombre.");
        }

        Quimicos quimico = quimicoOpt.get();

        for (BodegaDTO bodegaDTO : bodegaDTOs) {
            if (bodegaDTO.getName() == null || bodegaDTO.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la bodega no puede estar vacío.");
            }
            if (bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad en la bodega " + bodegaDTO.getName() + " nula.");
            }

            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega,
                    quimico);

            if (!bodegaQuimicosOpt.isPresent()) {
                bodegaQuimicoRepository.save(
                        new BodegaQuimicos(bodega, quimico, 0));
            }
        }

        for (BodegaDTO bodegaDTO : bodegaDTOs) {
            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega,
                    quimico);
            BodegaQuimicos bodegaQuimicos = bodegaQuimicosOpt.get();

            if (bodegaDTO.getQuantity() < 0) {
                bodegaQuimicos.setQuantity(bodegaDTO.getQuantity());
                bodegaQuimicoRepository.save(bodegaQuimicos);
                inventoryService.newItem(
                        quimico.getId().longValue(),
                        "quimico",
                        bodegaQuimicos.getQuantity().intValue(),
                        "damage",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se actualizo el inventario del quimico " + quimico.getName());
            } else {
                bodegaQuimicos.setQuantity(bodegaDTO.getQuantity() + bodegaQuimicos.getQuantity());
                bodegaQuimicoRepository.save(bodegaQuimicos);
                inventoryService.newItem(
                        quimico.getId().longValue(),
                        "quimico",
                        bodegaQuimicos.getQuantity().intValue(),
                        "restock",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se actualizo el inventario del quimico " + quimico.getName());
            }
        }

        return new QuimicosDTO(quimicosRepository.save(quimico));
    }

    public Quimicos updateInventoryQuimicoBatch(String nameJar, List<BodegaDTO> bodegaDTOs, String token) {

        Optional<Quimicos> quimicoOpt = quimicosRepository.findByName(nameJar.trim().toLowerCase());

        if (!quimicoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un químico con ese nombre.");
        }

        Quimicos quimico = quimicoOpt.get();

        for (BodegaDTO bodegaDTO : bodegaDTOs) {
            if (bodegaDTO.getName() == null || bodegaDTO.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la bodega no puede estar vacío.");
            }
            if (bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad en la bodega " + bodegaDTO.getName() + " nula.");
            }

            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega,
                    quimico);

            if (!bodegaQuimicosOpt.isPresent()) {
                bodegaQuimicoRepository.save(
                        new BodegaQuimicos(bodega, quimico, 0));
            }
        }

        for (BodegaDTO bodegaDTO : bodegaDTOs) {
            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega,
                    quimico);
            BodegaQuimicos bodegaQuimicos = bodegaQuimicosOpt.get();

            if (bodegaDTO.getQuantity() < 0) {
                bodegaQuimicos.setQuantity(bodegaDTO.getQuantity());
                bodegaQuimicoRepository.save(bodegaQuimicos);
                inventoryService.newItem(
                        quimico.getId().longValue(),
                        "quimico",
                        bodegaQuimicos.getQuantity().intValue(),
                        "damage",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se actualizo el inventario del quimico " + quimico.getName());
            } else {
                bodegaQuimicos.setQuantity(bodegaDTO.getQuantity());
                bodegaQuimicoRepository.save(bodegaQuimicos);
                inventoryService.newItem(
                        quimico.getId().longValue(),
                        "quimico",
                        bodegaQuimicos.getQuantity().intValue(),
                        "restock",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se actualizo el inventario del quimico " + quimico.getName());
            }
        }

        return quimico;
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

        for (BodegaDTO bodegaDTO : quimicoDTO.getBodegaName()) {
            if (bodegaDTO.getName() == null || bodegaDTO.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la bodega no puede estar vacío.");
            }
            if (bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad en la bodega " + bodegaDTO.getName() + " nula.");
            }

            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega,
                    quimico);

            if (!bodegaQuimicosOpt.isPresent()) {
                bodegaQuimicoRepository.save(
                        new BodegaQuimicos(bodega, quimico, 0));
            }
        }

        for (BodegaDTO bodegaDTO : quimicoDTO.getBodegaName()) {
            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega,
                    quimico);
            BodegaQuimicos bodegaQuimicos = bodegaQuimicosOpt.get();

            if (bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad a reabastecer debe ser especificada.");
            }

            if (bodegaDTO.getQuantity() < 0) {
                bodegaQuimicos.setQuantity(bodegaQuimicos.getQuantity() + bodegaDTO.getQuantity());
                bodegaQuimicoRepository.save(bodegaQuimicos);
                inventoryService.newItem(
                        quimico.getId().longValue(),
                        "quimico",
                        bodegaDTO.getQuantity().intValue(),
                        "damage",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se reportó un daño en el inventario del quimico " + quimico.getName()
                                + ", su inventario ahora es: " + bodegaQuimicos.getQuantity());
                continue;
            }

            bodegaQuimicos.setQuantity(bodegaQuimicos.getQuantity() + bodegaDTO.getQuantity());
            bodegaQuimicoRepository.save(bodegaQuimicos);
            inventoryService.newItem(
                    quimico.getId().longValue(),
                    "quimico",
                    bodegaDTO.getQuantity().intValue(),
                    "restock",
                    jwtService.getUserIdFromToken(token).intValue(),
                    "Se reabasteció el inventario del quimico " + quimico.getName());
        }

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
            return quimicosRepository.findByExactPrice(priceSearchRequest.getExactPrice(), pageable)
                    .map(QuimicosDTO::new);
        } else {
            return quimicosRepository
                    .findByPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable)
                    .map(QuimicosDTO::new);
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

        for (BodegaDTO bodegaDTO : quimicoDTO.getBodegaName()) {
            if (bodegaDTO.getName() == null || bodegaDTO.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la bodega no puede estar vacío.");
            }
            if (bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad en la bodega " + bodegaDTO.getName() + " nula.");
            }

            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());

            Optional<BodegaQuimicos> bodegaQuimicosOpt = bodegaQuimicoRepository.findByBodegaAndQuimico(bodega,
                    quimico);

            if (bodegaQuimicosOpt.isPresent()) {
                throw new IllegalArgumentException(
                        "El quimico " + quimico.getName() + " ya está asociado a la bodega " + bodega.getName());
            }
        }

        for (BodegaDTO bodegaDTO : quimicoDTO.getBodegaName()) {
            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());

            BodegaQuimicos bodegaQuimicos = new BodegaQuimicos(bodega, quimico,
                    bodegaDTO.getQuantity() != null ? bodegaDTO.getQuantity() : 0);
            bodegaQuimicoRepository.save(bodegaQuimicos);
        }

        return new QuimicosDTO(quimico);

    }

    public QuimicosDTO bodegaTranfer(BodegaMovementDTO request) {
        Quimicos quimico = quimicosRepository.findByName(request.getItemName().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un quimico con el nombre: " + request.getItemName()));

        if (request.getBodegaFrom().equalsIgnoreCase(request.getBodegaTo())) {
            throw new IllegalArgumentException("La bodega de origen y destino no pueden ser la misma.");
        }

        Bodega bodegaFrom = bodegaService.getBodegaByName(request.getBodegaFrom());
        Bodega bodegaTo = bodegaService.getBodegaByName(request.getBodegaTo());

        BodegaQuimicos bqFrom = bodegaQuimicoRepository.findByBodegaAndQuimico(bodegaFrom, quimico)
                .orElseThrow(() -> new IllegalArgumentException("El quimico " + quimico.getName()
                        + " no está asociado a la bodega de origen " + request.getBodegaFrom()));

        BodegaQuimicos bqTo = bodegaQuimicoRepository.findByBodegaAndQuimico(bodegaTo, quimico)
                .orElseThrow(() -> new IllegalArgumentException("El quimico " + quimico.getName()
                        + " no está asociado a la bodega de destino " + request.getBodegaTo()));

        if (bodegaFrom == null) {
            throw new IllegalArgumentException("El quimico " + quimico.getName()
                    + " no está asociado a la bodega de origen " + request.getBodegaFrom());
        }

        if (bodegaTo == null) {
            throw new IllegalArgumentException("El quimico " + quimico.getName()
                    + " no está asociado a la bodega de destino " + request.getBodegaTo());
        }

        if (bqFrom.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "No hay suficiente inventario en la bodega de origen. Inventario actual: " + bqFrom.getQuantity());
        }

        bqFrom.setQuantity(bqFrom.getQuantity() - request.getQuantity());
        bqTo.setQuantity(bqTo.getQuantity() + request.getQuantity());

        bodegaQuimicoRepository.save(bqFrom);
        bodegaQuimicoRepository.save(bqTo);

        return new QuimicosDTO(quimico);
    }

    public List<BodegaQuimicos> sortBodegaQuimicos(List<BodegaQuimicos> bodegaQuimicos) {
        try {
            return bodegaQuimicos.stream()
                    .filter(bq -> bq.getBodega() != null && bq.getBodega().getPriority() != null
                            && bq.getBodega().getPriority() > 0)
                    .sorted(Comparator.comparing(
                            bq -> bq.getBodega() != null ? bq.getBodega().getPriority() : null,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al ordenar las bodegas por prioridad: " + e.getMessage());
        }
    }

    public void batchSaveBodegaQuimicos(List<BodegaQuimicos> bodegaQuimicosList) {
        List<BodegaQuimicos> batchList = new ArrayList<>();
        for (BodegaQuimicos bodegaQuimicos : bodegaQuimicosList) {
            batchList.add(bodegaQuimicos);
            if (batchList.size() == 50) {
                bodegaQuimicoRepository.saveAll(batchList);
                batchList.clear();
            }
        }
        if (!batchList.isEmpty()) {
            bodegaQuimicoRepository.saveAll(batchList);
        }
    }
}