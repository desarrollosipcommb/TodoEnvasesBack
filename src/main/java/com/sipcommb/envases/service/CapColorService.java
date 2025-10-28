package com.sipcommb.envases.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.BodegaDTO;
import com.sipcommb.envases.dto.BodegaMovementDTO;
import com.sipcommb.envases.dto.CapColorDTO;
import com.sipcommb.envases.dto.CapColorRequest;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.entity.BodegaCapColor;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.CapColor;
import com.sipcommb.envases.repository.BodegaCapColorRepository;
import com.sipcommb.envases.repository.CapColorRepository;

@Service
@Transactional
public class CapColorService {

    @Autowired
    private CapColorRepository capColorRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private BodegaService bodegaService;

    @Autowired
    private BodegaCapColorRepository bodegaCapColorRepository;

    public Cap addCapColor(Cap cap, CapColorRequest request, String token) {

        List<CapColor> existingColors = cap.getColors();

        for (CapColor color : existingColors) {
            if (color.getColor().equalsIgnoreCase(request.getColor())) {
                throw new IllegalArgumentException(
                        "El color ya existe el color: " + request.getColor() + " en el tipo de tapa: " + cap.getName());
            }
        }

        if (request.getUnitPrice() == null || request.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        if (request.getDocenaPrice() != null && request.getDocenaPrice() < 0) {
            throw new IllegalArgumentException("El precio por docena no puede ser negativo.");
        }

        if (request.getCienPrice() != null && request.getCienPrice() < 0) {
            throw new IllegalArgumentException("El precio por cien no puede ser negativo.");
        }

        if (request.getPacaPrice() != null && request.getPacaPrice() < 0) {
            throw new IllegalArgumentException("El precio por paca no puede ser negativo.");
        }

        if (request.getUnitsInPaca() != null && request.getUnitsInPaca() < 0) {
            throw new IllegalArgumentException("El numero de unidades en una paca no puede ser negativo.");
        }

        for (BodegaDTO bodegaDTO : request.getBodega()) {
            bodegaService.getBodegaByName(bodegaDTO.getName());
            if (bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException(
                        "La cantidad de tapas en la bodega " + bodegaDTO.getName() + " no puede ser vacía.");
            }
        }

        CapColor newColor = new CapColor(
                request.getColor(),
                cap,
                request.getUnitPrice() != null ? request.getUnitPrice() : 0.00,
                request.getDocenaPrice() != null ? request.getDocenaPrice() : 0.00,
                request.getCienPrice() != null ? request.getCienPrice() : 0.00,
                request.getPacaPrice() != null ? request.getPacaPrice() : 0.00,
                request.getUnitsInPaca() != null ? request.getUnitsInPaca() : 0,
                new ArrayList<>());

        capColorRepository.save(newColor);

        for (BodegaDTO bodega : request.getBodega()) {
            Bodega bodegaEntity = bodegaService.getBodegaByName(bodega.getName());
            BodegaCapColor bodegaCapColor = bodegaCapColorRepository
                    .save(new BodegaCapColor(bodegaEntity, newColor, bodega.getQuantity()));

            newColor.getBodegas().add(bodegaCapColor);

            capColorRepository.save(newColor);

            capColorRepository.save(newColor);

            inventoryService.newItem(
                    newColor.getId() != null ? newColor.getId().longValue() : null,
                    "cap",
                    bodega.getQuantity().intValue(),
                    "restock",
                    jwtService.getUserIdFromToken(token).intValue(),
                    "Se añadió " + newColor.getCap().getName() + " " + newColor.getColor() + " al inventario");
        }

        return newColor.getCap();
    }

    public CapColorDTO updateCapColor(Cap cap, CapColorRequest request) {

        CapColor capColor = capColorRepository.findByCapAndColor(cap, request.getColor())
                .orElseThrow(
                        () -> new IllegalArgumentException("El color no existe en el tipo de tapa: " + cap.getName()));

        if (request.getUnitPrice() != null) {
            if (request.getUnitPrice() <= 0) {
                throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
            }
            capColor.setUnit_price(request.getUnitPrice());
        }

        if (request.getDocenaPrice() != null) {
            if (request.getDocenaPrice() < 0) {
                throw new IllegalArgumentException("El precio por docena no puede ser negativo.");
            }
            capColor.setDocena_price(request.getDocenaPrice());
        }

        if (request.getCienPrice() != null) {
            if (request.getCienPrice() < 0) {
                throw new IllegalArgumentException("El precio por cien no puede ser negativo.");
            }
            capColor.setCien_price(request.getCienPrice());
        }

        if (request.getPacaPrice() != null) {
            if (request.getPacaPrice() < 0) {
                throw new IllegalArgumentException("El precio por paca no puede ser negativo.");
            }
            capColor.setPaca_price(request.getPacaPrice());
        }

        if (request.getUnitsInPaca() != null) {
            if (request.getUnitsInPaca() < 0) {
                throw new IllegalArgumentException("El numero de unidades en una paca no puede ser negativo.");
            }
            capColor.setUnits_in_paca(request.getUnitsInPaca());
        }

        capColorRepository.save(capColor);

        for(BodegaDTO bodegaDTO : request.getBodega()){
            Bodega bodegaEntity = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaCapColor> bodegaCapColorOpt = bodegaCapColorRepository.findByBodegaIdAndCapColorId(bodegaEntity.getId(), capColor.getId());

            if(!bodegaCapColorOpt.isPresent()){
                throw new IllegalArgumentException("La bodega " + bodegaDTO.getName() + " no tiene registrado el color " + request.getColor() + " de la tapa " + cap.getName() + ".");
            }else{
                BodegaCapColor bodegaCapColor = bodegaCapColorOpt.get();
                if (bodegaDTO.getQuantity() != null) {
                    bodegaCapColor.setQuantity(bodegaDTO.getQuantity());
                    bodegaCapColorRepository.save(bodegaCapColor);
                }
            }
        }

        capColorRepository.save(capColor);

        return new CapColorDTO(capColor);
    }

    public CapColorDTO addCapToBodega(Cap cap, CapColorRequest request) {

        CapColor capColor = capColorRepository.findByCapAndColor(cap, request.getColor())
                .orElseThrow(
                        () -> new IllegalArgumentException("El color no existe en el tipo de tapa: " + cap.getName()));

        for(BodegaDTO bodegaDTO : request.getBodega()){
            Bodega bodegaEntity = bodegaService.getBodegaByName(bodegaDTO.getName());
            Optional<BodegaCapColor> bodegaCapColorOpt = bodegaCapColorRepository.findByBodegaIdAndCapColorId(bodegaEntity.getId(), capColor.getId());

            if(bodegaCapColorOpt.isPresent()){
                throw new IllegalArgumentException("El color " + request.getColor() + " ya existe en la bodega " + bodegaDTO.getName() + ".");
            }

            if (bodegaDTO.getQuantity() == null || bodegaDTO.getQuantity() < 0) {
                throw new IllegalArgumentException("La cantidad de tapas no puede ser negativa o vacía.");
            }

            BodegaCapColor newBodegaCapColor = new BodegaCapColor(bodegaEntity, capColor, bodegaDTO.getQuantity());
            bodegaCapColorRepository.save(newBodegaCapColor);
        }

        return new CapColorDTO(capColor);
    }

    /* CREO QUE ESTO NO SE USA
    public CapColorDTO updateCapColorInventory(Cap cap, CapColorRequest request, String token) {

        CapColor capColor = capColorRepository.findByCapAndColor(cap, request.getColor())
                .orElseThrow(
                        () -> new IllegalArgumentException("El color no existe en el tipo de tapa: " + cap.getName()));

        for(BodegaDTO bodegaDTO : request.getBodega()){

            if(bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad debe ser especificada.");
            }

            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());

            Optional<BodegaCapColor> bodegaCapColorOpt = bodegaCapColorRepository
                    .findByBodegaIdAndCapColorId(bodega.getId(), capColor.getId());

            if (!bodegaCapColorOpt.isPresent()) {
                throw new IllegalArgumentException(
                        "El color " + request.getColor() + " no existe en la bodega " + bodegaDTO.getName() + ".");
            }

            BodegaCapColor bodegaCapColor = bodegaCapColorOpt.get();

            if (bodegaDTO.getQuantity() < 0) {
                bodegaCapColor.setQuantity((bodegaDTO.getQuantity()));
                inventoryService.newItem(cap.getId(), "cap", bodegaDTO.getQuantity(),
                        "damage", jwtService.getUserIdFromToken(token).intValue(),
                        "Se a reportado un daño en el inventario de la tapa: " + cap.getName() + " " + capColor.getColor() +
                                ", su inventario ahora es: " + bodegaCapColor.getQuantity());
               
            }else{
                bodegaCapColor.setQuantity(bodegaDTO.getQuantity());
                inventoryService.newItem(
                        cap.getId(),
                        "cap",
                        bodegaDTO.getQuantity(),
                        "restock",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se actualizo el inventario de la tapa: " + cap.getName() + " " + capColor.getColor()
                                + ", su inventario ahora es: " + bodegaCapColor.getQuantity());
            }
        }
        
        return new CapColorDTO(capColorRepository.save(capColor));
    }

     */

    public void changeInventory(Cap cap, CapColorRequest request, String token) {
        CapColor capColor = capColorRepository.findByCapAndColor(cap, request.getColor())
                .orElseThrow(
                        () -> new IllegalArgumentException("El color no existe en el tipo de tapa: " + cap.getName()));

        for(BodegaDTO bodegaDTO : request.getBodega()){

            if(bodegaDTO.getQuantity() == null) {
                throw new IllegalArgumentException("La cantidad debe ser especificada.");
            }

            Bodega bodega = bodegaService.getBodegaByName(bodegaDTO.getName());

            Optional<BodegaCapColor> bodegaCapColorOpt = bodegaCapColorRepository
                    .findByBodegaIdAndCapColorId(bodega.getId(), capColor.getId());

            BodegaCapColor bodegaCapColor;

            if (!bodegaCapColorOpt.isPresent()) {
                bodegaCapColor = new BodegaCapColor(bodega, capColor, 0);
            } else {
                bodegaCapColor = bodegaCapColorOpt.get();
            }

            if (bodegaDTO.getQuantity() < 0) {
                bodegaCapColor.setQuantity((bodegaDTO.getQuantity() + bodegaCapColor.getQuantity()));
                inventoryService.newItem(cap.getId(), "cap", bodegaDTO.getQuantity(),
                        "damage", jwtService.getUserIdFromToken(token).intValue(),
                        "Se a reportado un daño en el inventario de la tapa: " + cap.getName() + " " + capColor.getColor() +
                                ", su inventario ahora es: " + bodegaCapColor.getQuantity());
               
            }else{
                bodegaCapColor.setQuantity((bodegaDTO.getQuantity() + bodegaCapColor.getQuantity()));
                inventoryService.newItem(
                        cap.getId(),
                        "cap",
                        bodegaDTO.getQuantity(),
                        "restock",
                        jwtService.getUserIdFromToken(token).intValue(),
                        "Se actualizo el inventario de la tapa: " + cap.getName() + " " + capColor.getColor()
                                + ", su inventario ahora es: " + bodegaCapColor.getQuantity());
            }
            bodegaCapColorRepository.save(bodegaCapColor);
        }
        capColorRepository.save(capColor);
    }

    public Page<CapColorDTO> getAllCapColors(Cap cap, String color, Pageable pageable) {
        return capColorRepository.findAllByCapAndColor(cap, color, pageable).map(CapColorDTO::new);
    }

    public Page<CapColorDTO> getAllCapColorsActive(Cap cap, String color, Pageable pageable) {
        return capColorRepository.findAllByCapActiveAndColor(cap, color, pageable).map(CapColorDTO::new);
    }

    public Page<CapColorDTO> getPriceRange(PriceSearchRequest priceSearchRequest, Pageable pageable) {

        boolean exactSearch = priceService.verifyPriceSearchRequest(priceSearchRequest);

        switch (priceSearchRequest.getPriceDeal()) {
            case CIEN:

                if (exactSearch) {
                    return capColorRepository.findByCienPrice(priceSearchRequest.getExactPrice(), pageable)
                            .map(CapColorDTO::new);
                }

                return capColorRepository.findByCienPriceBetween(priceSearchRequest.getMinPrice(),
                        priceSearchRequest.getMaxPrice(), pageable).map(CapColorDTO::new);

            case DOCENA:

                if (exactSearch) {
                    return capColorRepository.findByDocenaPrice(priceSearchRequest.getExactPrice(), pageable)
                            .map(CapColorDTO::new);
                }

                return capColorRepository.findByDocenaPriceBetween(priceSearchRequest.getMinPrice(),
                        priceSearchRequest.getMaxPrice(), pageable).map(CapColorDTO::new);

            case UNIDAD:
                if (exactSearch) {
                    return capColorRepository.findByUnidadPrice(priceSearchRequest.getExactPrice(), pageable)
                            .map(CapColorDTO::new);
                }

                return capColorRepository.findByUnidadPriceBetween(priceSearchRequest.getMinPrice(),
                        priceSearchRequest.getMaxPrice(), pageable).map(CapColorDTO::new);

            case PACA:
                if (exactSearch) {
                    return capColorRepository.findByPacaPrice(priceSearchRequest.getExactPrice(), pageable)
                            .map(CapColorDTO::new);
                }
                return capColorRepository.findByPacaPriceBetween(priceSearchRequest.getMinPrice(),
                        priceSearchRequest.getMaxPrice(), pageable).map(CapColorDTO::new);

            default:
                throw new IllegalArgumentException("Tipo de trato de precio no soportado.");

        }

    }

    public void deactivateCapColor(CapColor capColor) {
        capColor.setIs_active(false);
        capColorRepository.save(capColor);
    }

    public void activateCapColor(CapColor capColor) {
        capColor.setIs_active(true);
        capColorRepository.save(capColor);
    }

    public CapColorDTO deactivateCapColor(CapColorRequest request) {
        CapColor capColor = capColorRepository.findByNameAndColor(request.getName(), request.getColor())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "El color no existe en el tipo de tapa: " + request.getName()));
        capColor.setIs_active(false);
        return new CapColorDTO(capColorRepository.save(capColor));
    }

    public CapColorDTO activateCapColor(CapColorRequest request) {
        CapColor capColor = capColorRepository.findByNameAndColor(request.getName(), request.getColor())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "El color no existe o ya está activo en el tipo de tapa: " + request.getName()));
        capColor.setIs_active(true);
        return new CapColorDTO(capColorRepository.save(capColor));
    }

    public CapColorDTO BodegaTransfer(BodegaMovementDTO request) {
        CapColor capColor = capColorRepository.findByNameAndColor(request.getItemName(), request.getCapColor())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "El color no existe en el tipo de tapa: " + request.getItemName()));

        if (request.getBodegaFrom().equalsIgnoreCase(request.getBodegaTo())) {
            throw new IllegalArgumentException("Las bodegas de origen y destino no pueden ser las mismas.");
        }

        Bodega fromBodega = bodegaService.getBodegaByName(request.getBodegaFrom());
        Bodega toBodega = bodegaService.getBodegaByName(request.getBodegaTo());

        BodegaCapColor fromBodegaCapColor = bodegaCapColorRepository
                .findByBodegaIdAndCapColorId(fromBodega.getId(), capColor.getId())
                .orElseThrow(() -> new IllegalArgumentException("La tapa " + request.getItemName() + " de color "
                        + request.getCapColor() + " no existe en la bodega: " + request.getBodegaFrom() + "."));
        BodegaCapColor toBodegaCapColor = bodegaCapColorRepository
                .findByBodegaIdAndCapColorId(toBodega.getId(), capColor.getId())
                .orElseThrow(() -> new IllegalArgumentException("La tapa " + request.getItemName() + " de color "
                        + request.getCapColor() + " no existe en la bodega: " + request.getBodegaTo() + "."));

        if (fromBodegaCapColor == null) {
            throw new IllegalArgumentException("La tapa " + request.getItemName() + " de color " + request.getCapColor()
                    + " no existe en la bodega: " + request.getBodegaFrom() + ".");
        }

        if (toBodegaCapColor == null) {
            throw new IllegalArgumentException("La tapa " + request.getItemName() + " de color " + request.getCapColor()
                    + " no existe en la bodega: " + request.getBodegaTo() + ".");
        }

        if (fromBodegaCapColor != null && fromBodegaCapColor.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("No hay suficiente inventario para transferir. Inventario actual en "
                    + request.getBodegaFrom() + ": " + fromBodegaCapColor.getQuantity() + ".");
        }

        fromBodegaCapColor.setQuantity(fromBodegaCapColor.getQuantity() - request.getQuantity());
        toBodegaCapColor.setQuantity(toBodegaCapColor.getQuantity() + request.getQuantity());
        bodegaCapColorRepository.save(fromBodegaCapColor);
        bodegaCapColorRepository.save(toBodegaCapColor);
        capColorRepository.save(capColor);
        return new CapColorDTO(capColor);

    }

}
