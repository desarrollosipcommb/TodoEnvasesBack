package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.JarTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class CapService {

    @Autowired
    private CapRepository capRepository;

    @Autowired
    private JarTypeRepository jarTypeRepository;


    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JwtService jwtService;

    public CapDTO addCaps(CapRequest capRequest, String token) {

        if(capRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la tapa no puede estar vacío.");
        }

        if(capRequest.getQuantity() == null || capRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad de tapas debe ser mayor a cero.");
        }

        if(capRequest.getDiameter().isEmpty()) {
            capRequest.setDiameter("");
        }

        if(capRequest.getUnitPrice() == null || capRequest.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        if(capRequest.getDocenaPrice() != null && capRequest.getDocenaPrice() < 0) {
            throw new IllegalArgumentException("El precio por docena no puede ser negativo.");
        }

        if(capRequest.getCienPrice() != null && capRequest.getCienPrice() < 0) {
            throw new IllegalArgumentException("El precio por cien no puede ser negativo.");
        }

        if(capRequest.getPacaPrice() != null && capRequest.getPacaPrice() < 0) {
            throw new IllegalArgumentException("El precio por paca no puede ser negativo.");
        }

        if(capRequest.getUnitsInPaca() != null && capRequest.getUnitsInPaca() < 0) {
            throw new IllegalArgumentException("El numero de unidades en una paca no puede ser negativo.");
        }

        if(capRepository.findByNameAndDiameterAndColor(capRequest.getName(), capRequest.getDiameter(), capRequest.getColor()).isPresent()) {
            throw new RuntimeException("Ya existe una tapa con el mismo nombre, diametro y color");
        }

        Cap cap = new Cap();

        if(!capRequest.getDiameter().equals("") && !jarTypeRepository.getTypeByDiameter(capRequest.getDiameter()).isPresent()){
            throw new RuntimeException("No existe un tipo de frasco con el diámetro especificado.");
        }else{
            JarType jarType = jarTypeRepository.getTypeByDiameter(capRequest.getDiameter()).get();
            cap.setJarType(jarType);
        }

        
        cap.setName(capRequest.getName().toLowerCase().trim());
        cap.setDescription(capRequest.getDescription());    
        cap.setColor(capRequest.getColor());
        cap.setQuantity(capRequest.getQuantity());
        cap.setUnitPrice(BigDecimal.valueOf(capRequest.getUnitPrice()));
        cap.setDocenaPrice(BigDecimal.valueOf(capRequest.getDocenaPrice()));
        cap.setCienPrice(BigDecimal.valueOf(capRequest.getCienPrice()));
        cap.setPacaPrice(BigDecimal.valueOf(capRequest.getPacaPrice()));
        cap.setUnitsInPaca(capRequest.getUnitsInPaca());
        cap = capRepository.save(cap);

        inventoryService.newItem(cap.getId(), "cap", cap.getQuantity().intValue(), "restock", jwtService.getUserIdFromToken(token).intValue(), "Se añadio "+cap.getName()+" al inventario");
        CapDTO capDTO = new CapDTO(cap);

        return capDTO;
    }

    public List<CapDTO> getAllCaps() {
        List<Cap> caps = capRepository.findAll();
        return caps.stream().map(CapDTO::new).collect(Collectors.toList());
    }

    public List<CapDTO> getAllActiveCaps(){
        List<Cap> caps = capRepository.findAllByIsActive();

        return caps.stream().map(CapDTO::new).collect(Collectors.toList());
    }

    public List<CapDTO> getAllInactiveCaps(){
        List<Cap> caps = capRepository.findAllByIsActiveFalse();

        return caps.stream().map(CapDTO::new).collect(Collectors.toList());
    }

    public List<CapDTO> getCapsByDiameter(String diameter) {
        if(capRepository.getFromCapDiameter(diameter).get().isEmpty()) {
            throw new RuntimeException("No existen tapas con el diámetro especificado.");
        }
        List<Cap> caps = capRepository.getFromCapDiameter(diameter).get();
        return caps.stream().map(CapDTO::new).collect(Collectors.toList());
    }

    public List<CapDTO> getByName(String name) {
        if(capRepository.getFromNameLike(name).get().isEmpty()) {
            throw new RuntimeException("No existen tapas con el nombre especificado.");
        }
        List<Cap> caps = capRepository.getFromNameLike(name).get();
        return caps.stream().map(CapDTO::new).collect(Collectors.toList());
    }

    public List<CapDTO> getByColor(String color) {
        if(capRepository.getFromColor(color).get().isEmpty()) {
            throw new RuntimeException("No existen tapas con el color especificado.");
        }
        List<Cap> caps = capRepository.getFromColor(color).get();
        return caps.stream().map(CapDTO::new).collect(Collectors.toList());
    }


    public CapDTO updateCap(CapRequest capRequest, String token) {

        Optional<Cap> capOptional = capRepository.findByNameAndDiameterAndColor(capRequest.getName(), capRequest.getDiameter(), capRequest.getColor());

        if(!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }

        Cap cap = capOptional.get();

        if(capRequest.getQuantity() != null) {
            cap.setQuantity(capRequest.getQuantity());
            inventoryService.newItem(cap.getId(), "cap", cap.getQuantity().intValue(), "adjustment", jwtService.getUserIdFromToken(token).intValue(), "Se actualizo la tapa: "+cap.getName());

        }

        if(capRequest.getDescription() != null && !capRequest.getDescription().isEmpty()) {
            cap.setDescription(capRequest.getDescription());
        }

        if(capRequest.getUnitPrice() != null && capRequest.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        } else if(capRequest.getUnitPrice() != null) {
            cap.setUnitPrice(BigDecimal.valueOf(capRequest.getUnitPrice()));
        }

        if(capRequest.getDocenaPrice() != null && capRequest.getDocenaPrice() < 0) {
            throw new IllegalArgumentException("El precio por docena no puede ser negativo.");
        } else if(capRequest.getDocenaPrice() != null) {
            cap.setDocenaPrice(BigDecimal.valueOf(capRequest.getDocenaPrice()));
        }

        if(capRequest.getCienPrice() != null && capRequest.getCienPrice() < 0) {
            throw new IllegalArgumentException("El precio por cien no puede ser negativo.");
        } else if(capRequest.getCienPrice() != null) {
            cap.setCienPrice(BigDecimal.valueOf(capRequest.getCienPrice()));
        }

        if(capRequest.getPacaPrice() != null && capRequest.getPacaPrice() < 0) {
            throw new IllegalArgumentException("El precio por paca no puede ser negativo.");
        } else if(capRequest.getPacaPrice() != null) {
            cap.setPacaPrice(BigDecimal.valueOf(capRequest.getPacaPrice()));    
        }

        if(capRequest.getUnitsInPaca() != null && capRequest.getUnitsInPaca() < 0) {
            throw new IllegalArgumentException("El numero de unidades en una paca no puede ser negativo.");
        } else if(capRequest.getUnitsInPaca() != null) {
            cap.setUnitsInPaca(capRequest.getUnitsInPaca());
        }

        cap.setDescription(capRequest.getDescription());    
        cap.setUpdatedAt(LocalDateTime.now());
        capRepository.save(cap);

                

        return new CapDTO(cap);
    }

    public CapDTO deleteCap(CapRequest capRequest) {

        Optional<Cap> capOptional = capRepository.findByNameAndDiameterAndColor(capRequest.getName(), capRequest.getDiameter(), capRequest.getColor());
        if(!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }
        Cap cap = capOptional.get();
        cap.setUpdatedAt(LocalDateTime.now());
        cap.setIsActive(false);
        capRepository.save(cap);
        return new CapDTO(cap);
    }

    public CapDTO activateCap(CapRequest capRequest) {

        Optional<Cap> capOptional = capRepository.findByNameAndDiameterAndColor(capRequest.getName(), capRequest.getDiameter(), capRequest.getColor());
        if(!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }
        Cap cap = capOptional.get();
        cap.setIsActive(true);
        cap.setUpdatedAt(LocalDateTime.now());
        capRepository.save(cap);
        return new CapDTO(cap);
    }


    public CapDTO changeInventory(Long id, String transactionType, int quantity, String description, String token) {
        Optional<Cap> capOptional = capRepository.findById(id);
        if(!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con este ID.");
        }
        Cap cap = capOptional.get();
        cap.setQuantity(cap.getQuantity() + quantity);
        token = token.replace("Bearer ", "").trim();
        inventoryService.newItem(cap.getId(), "cap", quantity, transactionType, jwtService.getUserIdFromToken(token).intValue(), description);
        capRepository.save(cap);
        return new CapDTO(cap);
    }

}
