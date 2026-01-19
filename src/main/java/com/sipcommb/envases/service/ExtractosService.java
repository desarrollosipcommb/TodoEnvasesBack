package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.BodegaDTO;
import com.sipcommb.envases.dto.BodegaMovementDTO;
import com.sipcommb.envases.dto.ExtractoRequest;
import com.sipcommb.envases.dto.ExtractosDTO;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.entity.BodegaExtractos;
import com.sipcommb.envases.entity.Extractos;
import com.sipcommb.envases.repository.BodegaExtractoRepository;
import com.sipcommb.envases.repository.ExtractosRepository;

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
public class ExtractosService {

    @Autowired
    private ExtractosRepository extractosRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private BodegaService bodegaService;

    @Autowired
    private BodegaExtractoRepository bodegaExtractoRepository;

    // Method to add a new extracto
    public ExtractosDTO addExtracto(ExtractoRequest extractosDTO, String token) {

        if (extractosDTO.getName() == null || extractosDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del extracto no puede estar vacío.");
        }

        Optional<Extractos> existingExtracto = extractosRepository
                .findByName(extractosDTO.getName().trim().toLowerCase());

        if (existingExtracto.isPresent()) {
            throw new IllegalArgumentException("Ya existe un extracto con el nombre: " + extractosDTO.getName());
        }

        if (extractosDTO.getPrice22ml() == null || extractosDTO.getPrice22ml() <= 0) {
            throw new IllegalArgumentException("El precio para 22ml no puede ser negativo o cero.");
        }

        for (BodegaDTO bodegaDTO : extractosDTO.getBodega()) {
            if (bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad en bodega no puede ser vacía.");
            }
            bodegaService.getBodegaByName(bodegaDTO.getName());
        }

        Extractos newExtracto = new Extractos();
        newExtracto.setName(extractosDTO.getName().trim().toLowerCase());
        newExtracto.setPrice22ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice22ml()));
        newExtracto.setPrice60ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice60ml()));
        newExtracto.setPrice125ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice125ml()));
        newExtracto.setPrice250ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice250ml()));
        newExtracto.setPrice500ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice500ml()));
        newExtracto.setPrice1000ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice1000ml()));
        newExtracto.setDescription(
                extractosDTO.getDescription() != null ? extractosDTO.getDescription() : "Sin descripción");
        newExtracto.setActive(true);

        extractosRepository.save(newExtracto);
        String cleanToken = token.trim().replace("Bearer ", "");
        List<BodegaExtractos> bodegaExtractosList = new ArrayList<>();
        for (BodegaDTO extractosBodegaDTO : extractosDTO.getBodega()) {
            Bodega bodega = bodegaService.getBodegaByName(extractosBodegaDTO.getName());
            BodegaExtractos bodegaExtractos = bodegaExtractoRepository
                    .save(new BodegaExtractos(bodega, newExtracto, extractosBodegaDTO.getQuantity()));
            bodegaExtractosList.add(bodegaExtractos);
            newExtracto.getBodegas().add(bodegaExtractos);

            inventoryService.newItem(
                    newExtracto.getId() != null ? newExtracto.getId().longValue() : null,
                    "extracto",
                    extractosBodegaDTO.getQuantity().intValue(),
                    "restock",
                    jwtService.getUserIdFromToken(cleanToken).intValue(),
                    "Se añadió " + newExtracto.getName() + " al inventario");
        }
        batchSaveBodegaExtractos(bodegaExtractosList);
        extractosRepository.save(newExtracto);
        ExtractosDTO extractosReturn = new ExtractosDTO(newExtracto);
        return extractosReturn;
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

    public Page<ExtractosDTO> getExtractosLikeNameActive(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            return extractosRepository.findAll(pageable).map(ExtractosDTO::new);
        }
        return extractosRepository.findLikeNameActive(name.trim(), pageable).map(ExtractosDTO::new);
    }

    public ExtractosDTO updateExtracto(ExtractoRequest extractosDTO, String token) {

        if (extractosDTO.getName() == null || extractosDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del extracto no puede estar vacío.");
        }

        Optional<Extractos> existingExtracto = extractosRepository
                .findByName(extractosDTO.getName().trim().toLowerCase());

        if (!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + extractosDTO.getName());
        }

        Extractos extractoToUpdate = existingExtracto.get();

        if (extractosDTO.getDescription() != null) {
            extractoToUpdate.setDescription(extractosDTO.getDescription());
        }

        if (extractosDTO.getPrice22ml() != null && extractosDTO.getPrice22ml() > 0) {
            extractoToUpdate.setPrice22ml(java.math.BigDecimal.valueOf(extractosDTO.getPrice22ml()));
        } else if (extractosDTO.getPrice22ml() != null && extractosDTO.getPrice22ml() < 0) {
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

        for (BodegaDTO bodegaDTO : extractosDTO.getBodega()) {
            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaExtractos> bodegaExtractosOpt = bodegaExtractoRepository.findByBodegaAndExtracto(bodega,
                    extractoToUpdate);

            if (!bodegaExtractosOpt.isPresent()) {
                throw new IllegalArgumentException("El extracto no está asociado a la bodega: " + bodegaDTO.getName());
            }
            bodegaExtractosOpt.get().setQuantity(bodegaDTO.getQuantity());

            if (bodegaDTO.getQuantity() < 0) {
                bodegaExtractosOpt.get().setQuantity(bodegaDTO.getQuantity());
                bodegaExtractoRepository.save(bodegaExtractosOpt.get());
                inventoryService.newItem(
                        extractoToUpdate.getId().longValue(),
                        "extracto",
                        bodegaDTO.getQuantity().intValue(),
                        "damage",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se actualizo el inventario del extracto " + extractoToUpdate.getName()
                                + ", su inventario ahora es: " + bodegaDTO.getQuantity());
            } else {
                inventoryService.newItem(
                        extractoToUpdate.getId().longValue(),
                        "extracto",
                        bodegaDTO.getQuantity().intValue(),
                        "restock",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se ha reabastecido el extracto " + extractoToUpdate.getName() + ", su inventario ahora es: "
                                + bodegaDTO.getQuantity());
            }
            bodegaExtractoRepository.save(bodegaExtractosOpt.get());
        }

        extractosRepository.save(extractoToUpdate);
        return new ExtractosDTO(extractoToUpdate);
    }

    public ExtractosDTO changeInventory(ExtractoRequest extractosDTO, String token) {

        if (extractosDTO.getName() == null || extractosDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del extracto no puede estar vacío.");
        }

        Optional<Extractos> existingExtracto = extractosRepository
                .findByName(extractosDTO.getName().trim().toLowerCase());

        if (!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + extractosDTO.getName());
        }

        Extractos extractoToRestock = existingExtracto.get();

        for (BodegaDTO bodegaDTO : extractosDTO.getBodega()) {
            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());

            Optional<BodegaExtractos> bodegaExtractosOpt = bodegaExtractoRepository.findByBodegaAndExtracto(bodega,
                    existingExtracto.get());

            BodegaExtractos bodegaExtractos = null;
            if (!bodegaExtractosOpt.isPresent()) {
                bodegaExtractos = new BodegaExtractos(bodega, existingExtracto.get(), 0);
            } else {
                bodegaExtractos = bodegaExtractosOpt.get();
            }

            if (bodegaDTO.getQuantity() == null || bodegaDTO.getQuantity() == 0) {
                throw new IllegalArgumentException("La cantidad a reabastecer debe ser especificada.");
            }

            if (bodegaDTO.getQuantity() < 0) {
                bodegaExtractos.setQuantity(bodegaExtractos.getQuantity() + bodegaDTO.getQuantity());
                bodegaExtractoRepository.save(bodegaExtractos);
                inventoryService.newItem(
                        extractoToRestock.getId().longValue(),
                        "extracto",
                        bodegaDTO.getQuantity().intValue(),
                        "damage",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se ha reportado un daño en el extracto " + extractoToRestock.getName()
                                + ", su inventario ahora es: " + bodegaExtractos.getQuantity());
                continue;
            }

            bodegaExtractos.setQuantity(bodegaExtractos.getQuantity() + bodegaDTO.getQuantity());
            bodegaExtractoRepository.save(bodegaExtractos);
            inventoryService.newItem(
                    extractoToRestock.getId().longValue(),
                    "extracto",
                    bodegaDTO.getQuantity().intValue(),
                    "restock",
                    jwtService.getUserIdFromToken(token).intValue(),
                    "hay " + bodegaDTO.getQuantity() + " unidades nuevas de " + extractoToRestock.getName()
                            + " en el inventario, en total hay " + bodegaExtractos.getQuantity()
                            + " unidades disponibles.");
        }

        return new ExtractosDTO(extractosRepository.save(extractoToRestock));
    }

    // el que se usa para excel
    public ExtractosDTO updateExtractoInventorys(ExtractoRequest extractosDTO, String token) {

        if (extractosDTO.getName() == null || extractosDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del extracto no puede estar vacío.");
        }

        Optional<Extractos> existingExtracto = extractosRepository
                .findByName(extractosDTO.getName().trim().toLowerCase());

        if (!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + extractosDTO.getName());
        }

        Extractos extractoToRestock = existingExtracto.get();

        for (BodegaDTO bodegaDTO : extractosDTO.getBodega()) {
            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());

            Optional<BodegaExtractos> bodegaExtractosOpt = bodegaExtractoRepository.findByBodegaAndExtracto(bodega,
                    existingExtracto.get());

            BodegaExtractos bodegaExtractos = null;
            if (!bodegaExtractosOpt.isPresent()) {
                bodegaExtractos = new BodegaExtractos(bodega, existingExtracto.get(), 0);
            } else {
                bodegaExtractos = bodegaExtractosOpt.get();
            }

            if (bodegaDTO.getQuantity() == null || bodegaDTO.getQuantity() == 0) {
                throw new IllegalArgumentException("La cantidad a reabastecer debe ser especificada.");
            }

            if (bodegaDTO.getQuantity() < 0) {
                bodegaExtractos.setQuantity(bodegaDTO.getQuantity());
                bodegaExtractoRepository.save(bodegaExtractos);
                inventoryService.newItem(
                        extractoToRestock.getId().longValue(),
                        "extracto",
                        bodegaDTO.getQuantity().intValue(),
                        "damage",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se ha reportado un daño en el extracto " + extractoToRestock.getName()
                                + ", su inventario ahora es: " + bodegaExtractos.getQuantity());
                continue;
            }

            bodegaExtractos.setQuantity(bodegaDTO.getQuantity() + bodegaExtractos.getQuantity());
            bodegaExtractoRepository.save(bodegaExtractos);
            inventoryService.newItem(
                    extractoToRestock.getId().longValue(),
                    "extracto",
                    bodegaDTO.getQuantity().intValue(),
                    "restock",
                    jwtService.getUserIdFromToken(token).intValue(),
                    "hay " + bodegaDTO.getQuantity() + " unidades nuevas de " + extractoToRestock.getName()
                            + " en el inventario, en total hay " + bodegaExtractos.getQuantity()
                            + " unidades disponibles.");
        }

        return new ExtractosDTO(extractosRepository.save(extractoToRestock));
    }

    public ExtractosDTO deactivateExtracto(String name) {
        Optional<Extractos> existingExtracto = extractosRepository.findByName(name.trim().toLowerCase());

        if (!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + name);
        }

        Extractos extractoToDeactivate = existingExtracto.get();
        extractoToDeactivate.setActive(false);
        extractosRepository.save(extractoToDeactivate);

        return new ExtractosDTO(extractoToDeactivate);
    }

    public ExtractosDTO activateExtracto(String name) {
        Optional<Extractos> existingExtracto = extractosRepository.findByName(name.trim().toLowerCase());

        if (!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + name);
        }

        Extractos extractoToActivate = existingExtracto.get();
        extractoToActivate.setActive(true);
        extractosRepository.save(extractoToActivate);

        return new ExtractosDTO(extractoToActivate);
    }

    public Page<ExtractosDTO> getPriceRange(PriceSearchRequest priceSearchRequest, Pageable pageable) {
        boolean exactSearch = priceService.verifyPriceSearchRequest(priceSearchRequest);

        switch (priceSearchRequest.getPriceDeal()) {
            case ML_22:
                if (exactSearch) {
                    return extractosRepository.findByPrice22ml(priceSearchRequest.getExactPrice(), pageable)
                            .map(ExtractosDTO::new);
                } else {
                    return extractosRepository.findByPrice22mlBetween(priceSearchRequest.getMinPrice(),
                            priceSearchRequest.getMaxPrice(), pageable).map(ExtractosDTO::new);
                }
            case ML_60:
                if (exactSearch) {
                    return extractosRepository.findByPrice60ml(priceSearchRequest.getExactPrice(), pageable)
                            .map(ExtractosDTO::new);
                } else {
                    return extractosRepository.findByPrice60mlBetween(priceSearchRequest.getMinPrice(),
                            priceSearchRequest.getMaxPrice(), pageable).map(ExtractosDTO::new);
                }
            case ML_125:
                if (exactSearch) {
                    return extractosRepository.findByPrice125ml(priceSearchRequest.getExactPrice(), pageable)
                            .map(ExtractosDTO::new);
                } else {
                    return extractosRepository.findByPrice125mlBetween(priceSearchRequest.getMinPrice(),
                            priceSearchRequest.getMaxPrice(), pageable).map(ExtractosDTO::new);
                }
            case ML_500:
                if (exactSearch) {
                    return extractosRepository.findByPrice500ml(priceSearchRequest.getExactPrice(), pageable)
                            .map(ExtractosDTO::new);
                } else {
                    return extractosRepository.findByPrice500mlBetween(priceSearchRequest.getMinPrice(),
                            priceSearchRequest.getMaxPrice(), pageable).map(ExtractosDTO::new);
                }
            case ML_1000:
                if (exactSearch) {
                    return extractosRepository.findByPrice1000ml(priceSearchRequest.getExactPrice(), pageable)
                            .map(ExtractosDTO::new);
                } else {
                    return extractosRepository.findByPrice1000mlBetween(priceSearchRequest.getMinPrice(),
                            priceSearchRequest.getMaxPrice(), pageable).map(ExtractosDTO::new);
                }
            default:
                throw new IllegalArgumentException(
                        "Tipo de trato de precio no soportado: " + priceSearchRequest.getPriceDeal());
        }
    }

    public ExtractosDTO addBodegaToExtracto(ExtractoRequest extractoRequest) {
        Optional<Extractos> existingExtracto = extractosRepository
                .findByName(extractoRequest.getName().trim().toLowerCase());

        if (!existingExtracto.isPresent()) {
            throw new IllegalArgumentException("No existe un extracto con el nombre: " + extractoRequest.getName());
        }

        for (BodegaDTO bodegaDTO : extractoRequest.getBodega()) {
            bodegaService.getBodegaByName(bodegaDTO.getName());

            Optional<BodegaExtractos> bodegaExtractosOpt = bodegaExtractoRepository.findByBodegaAndExtracto(
                    bodegaService.getBodegaByName(bodegaDTO.getName()), existingExtracto.get());

            if (bodegaExtractosOpt.isPresent()) {
                throw new IllegalArgumentException("El extracto ya está asociado a la bodega: " + bodegaDTO.getName());
            }

            BodegaExtractos bodegaExtractos = new BodegaExtractos(
                    bodegaService.getBodegaByName(bodegaDTO.getName()),
                    existingExtracto.get(),
                    bodegaDTO.getQuantity());

            bodegaExtractoRepository.save(bodegaExtractos);
            existingExtracto.get().getBodegas().add(bodegaExtractos);

        }

        extractosRepository.save(existingExtracto.get());
        return new ExtractosDTO(existingExtracto.get());
    }

    public ExtractosDTO bodegaTransfer(BodegaMovementDTO request) {
        Extractos extracto = extractosRepository.findByName(request.getItemName().trim().toLowerCase())
                .orElseThrow(
                        () -> new IllegalArgumentException("El extracto no existe: " + request.getItemName()));

        if (request.getBodegaFrom().equalsIgnoreCase(request.getBodegaTo())) {
            throw new IllegalArgumentException("Las bodegas de origen y destino no pueden ser las mismas.");
        }

        Bodega fromBodega = bodegaService.getBodegaByName(request.getBodegaFrom());
        Bodega toBodega = bodegaService.getBodegaByName(request.getBodegaTo());

        BodegaExtractos fromBodegaExtracto = bodegaExtractoRepository.findByBodegaAndExtracto(fromBodega, extracto)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "El extracto no existe en la bodega de origen: " + request.getBodegaFrom()));

        BodegaExtractos toBodegaExtracto = bodegaExtractoRepository.findByBodegaAndExtracto(toBodega, extracto)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "El extracto no existe en la bodega de destino: " + request.getBodegaTo()));

        if (fromBodegaExtracto == null) {
            throw new IllegalArgumentException(
                    "El extracto no existe en la bodega de origen: " + request.getBodegaFrom());
        }

        if (toBodegaExtracto == null) {
            throw new IllegalArgumentException(
                    "El extracto no existe en la bodega de destino: " + request.getBodegaTo());
        }

        if (fromBodegaExtracto.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "No hay suficiente inventario en la bodega de origen para transferir la cantidad solicitada.");
        }

        fromBodegaExtracto.setQuantity(fromBodegaExtracto.getQuantity() - request.getQuantity());
        toBodegaExtracto.setQuantity(toBodegaExtracto.getQuantity() + request.getQuantity());
        bodegaExtractoRepository.save(fromBodegaExtracto);
        bodegaExtractoRepository.save(toBodegaExtracto);
        extractosRepository.save(extracto);
        return new ExtractosDTO(extracto);
    }

    public List<BodegaExtractos> sortBodegaExtractos(List<BodegaExtractos> bodegaExtractosList) {
        try {
            return bodegaExtractosList.stream()
                    .filter(be -> be.getBodega() != null && be.getBodega().getPriority() != null
                            && be.getBodega().getPriority() > 0)
                    .sorted(Comparator.comparing(
                            be -> be.getBodega() != null ? be.getBodega().getPriority() : null,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al ordenar las bodegas por prioridad: " + e.getMessage());
        }
    }

    public void batchSaveBodegaExtractos(List<BodegaExtractos> bodegaExtractosList) {
        List<BodegaExtractos> batchList = new ArrayList<>();
        for(BodegaExtractos bodegaExtractos : bodegaExtractosList) {
            batchList.add(bodegaExtractos);
            if(batchList.size() == 50) {
                bodegaExtractoRepository.saveAll(batchList);
                batchList.clear();
            }
        }
        if(!batchList.isEmpty()) {
            bodegaExtractoRepository.saveAll(batchList);
        }
    }

}
