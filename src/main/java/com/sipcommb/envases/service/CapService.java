package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.CapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@Transactional
public class CapService {

    @Autowired
    private CapRepository capRepository;

    @Autowired
    private JarTypeService jarTypeService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JwtService jwtService;

    public CapDTO addCaps(CapRequest capRequest, String token) {
        if(capRequest.getQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad de tapas no puede ser negativa.");
        }

        if(!jarTypeService.getJarTypeByName(capRequest.getTypeName()).isPresent()) {
            throw new IllegalArgumentException("El tipo de tarro especificado no existe.");
        }

        if(capRequest.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        if(capRepository.existsByName(capRequest.getName())) {
            throw new IllegalArgumentException("Ya existe una tapa con el nombre especificado.");
        }
        JarType jarType = jarTypeService.getJarTypeByName(capRequest.getTypeName()).get();
       
        Cap cap = new Cap();
        cap.setName(capRequest.getName());
        cap.setDescription(capRequest.getDescription());
        cap.setJarType(jarType);
        cap.setColor(capRequest.getColor());
        cap.setQuantity(capRequest.getQuantity());
        cap.setUnitPrice(BigDecimal.valueOf(capRequest.getUnitPrice()));
        cap = capRepository.save(cap);

        inventoryService.newItem(cap.getId(), "cap", cap.getQuantity().intValue(), "restock", jwtService.getUserIdFromToken(token).intValue(), "Se añadio "+cap.getName()+" al inventario");
        CapDTO capDTO = new CapDTO(cap);

        return capDTO;
    }

}
