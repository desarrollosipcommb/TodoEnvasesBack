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
import java.util.List;
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
        if(capRequest.getQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad de tapas no puede ser negativa.");
        }

        if(capRequest.getDiameter().isEmpty()) {
            capRequest.setDiameter("");
        }

        if(capRequest.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        if(capRequest.getDocenaPrice() < 0) {
            throw new IllegalArgumentException("El precio por docena no puede ser negativo.");
        }

        if(capRequest.getCienPrice() < 0) {
            throw new IllegalArgumentException("El precio por cien no puede ser negativo.");
        }

        if(capRequest.getPacaPrice() < 0) {
            throw new IllegalArgumentException("El precio por paca no puede ser negativo.");
        }

        if(capRequest.getUnitsInPaca() < 0) {
            throw new IllegalArgumentException("El numero de unidades en una paca no puede ser negativo.");
        }

        if(capRepository.findByNameAndDiameterAndColor(capRequest.getName(), capRequest.getDiameter(), capRequest.getColor()).isPresent()) {
            throw new RuntimeException("Ya existe una tapa con el mismo nombre, diametro y color");
        }

        Cap cap = new Cap();

        if(!capRequest.getDiameter().isEmpty() && !jarTypeRepository.getTypeByDiameter(capRequest.getDiameter()).isPresent()){
            throw new RuntimeException("No existe un tipo de frasco con el diámetro especificado.");
        }else{
            JarType jarType = jarTypeRepository.getTypeByDiameter(capRequest.getDiameter()).get();
            cap.setJarType(jarType);
        }

        
        cap.setName(capRequest.getName());
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

}
