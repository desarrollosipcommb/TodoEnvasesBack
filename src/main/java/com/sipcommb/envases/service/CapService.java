package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.JarTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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

    @Autowired
    private PriceService priceService;

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

        if(capRequest.getColor() == null || capRequest.getColor().isEmpty() || capRequest.getColor().equals("")) {
            throw new IllegalArgumentException("El color es obligatorio.");
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

    public Page<CapDTO> getAllCaps(Pageable pageable) {
        Page<Cap> caps = capRepository.findAll(pageable);
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getAllActiveCaps(Pageable pageable) {
        Page<Cap> caps = capRepository.findAllByIsActiveTrue(pageable);
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getAllInactiveCaps(Pageable pageable) {
        Page<Cap> caps = capRepository.findAllByIsActiveFalse(pageable);
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getCapsByDiameter(String diameter, Pageable pageable) {
        Page<Cap> caps = capRepository.getFromCapDiameter(diameter, pageable).get();
        if(caps.isEmpty()) {
            throw new RuntimeException("No existen tapas con el diámetro especificado.");
        }
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getByName(String name, Pageable pageable) {
        Page<Cap> caps = capRepository.getFromNameLike(name, pageable).get();
        if(caps.isEmpty()) {
            throw new RuntimeException("No existen tapas con el nombre especificado.");
        }
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getByColor(String color, Pageable pageable) {
        Page<Cap> caps = capRepository.getFromColor(color, pageable).get();
        if(caps.isEmpty()) {
            throw new RuntimeException("No existen tapas con el color especificado.");
        }
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getFromNameLikeAndColorAndNameDiameter(String name,String color,String diameter , Pageable pageable) {
      Page<Cap> caps = capRepository.getFromNameLikeAndColorAndDiameter(name,color,diameter, pageable);
      return caps.map(CapDTO::new);
    }


    public CapDTO updateCap(CapRequest capRequest, String token) {

        Optional<Cap> capOptional = capRepository.findByNameAndDiameterAndColor(capRequest.getName(), capRequest.getDiameter(), capRequest.getColor());

        if(!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }

        Cap cap = capOptional.get();

        if(capRequest.getQuantity() != null) {
            cap.setQuantity(capRequest.getQuantity());
            inventoryService.newItem(cap.getId(), "cap", cap.getQuantity().intValue(), "adjustment", jwtService.getUserIdFromToken(token).intValue(), "Se actualizo la tapa: "+cap.getName()+", su inventario ahora es: "+cap.getQuantity());

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

        Optional<Cap> capOptional = capRepository.findByNameAndDiameterAndColor2(capRequest.getName(), capRequest.getDiameter(), capRequest.getColor());
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

        Optional<Cap> capOptional = capRepository.findByNameAndDiameterAndColor2(capRequest.getName(), capRequest.getDiameter(), capRequest.getColor());
        if(!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }
        Cap cap = capOptional.get();
        cap.setIsActive(true);
        cap.setUpdatedAt(LocalDateTime.now());
        capRepository.save(cap);
        return new CapDTO(cap);
    }


    public CapDTO changeInventory(CapRequest capRequest, String token) {
        Optional<Cap> capOptional = capRepository.findByNameAndDiameterAndColor(capRequest.getName(), capRequest.getDiameter(), capRequest.getColor());
        if(!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }
        Cap cap = capOptional.get();
        
        if(capRequest.getQuantity() == null) {
            throw new IllegalArgumentException("La cantidad debe ser especificada.");
        }

        if(capRequest.getQuantity() < 0) {
            cap.setQuantity(cap.getQuantity() + capRequest.getQuantity());
            inventoryService.newItem(cap.getId(), "cap", capRequest.getQuantity().intValue(), "damage", jwtService.getUserIdFromToken(token).intValue(), "Se a reportado un daño en el inventario de la tapa: "+cap.getName()+", su inventario ahora es: "+cap.getQuantity());
            return new CapDTO(capRepository.save(cap));
        }

        cap.setQuantity(cap.getQuantity() + capRequest.getQuantity());
        inventoryService.newItem(
            cap.getId(),
            "cap",
            capRequest.getQuantity().intValue(),
            "restock",
            jwtService.getUserIdFromToken(token).intValue(),
            "Se actualizo el inventario de la tapa: "+cap.getName()+", su inventario ahora es: "+cap.getQuantity()
        );

        return new CapDTO(cap);
    }

    public Page<CapDTO> getPriceRange(PriceSearchRequest priceSearchRequest, Pageable pageable) {

        boolean exactSearch = priceService.verifyPriceSearchRequest(priceSearchRequest);

        switch (priceSearchRequest.getPriceDeal()) {
            case CIEN:

                if(exactSearch) {
                    return capRepository.findByCienPrice(priceSearchRequest.getExactPrice(), pageable).map(CapDTO::new);
                }
                
                return capRepository.findByCienPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(CapDTO::new);
    
            case DOCENA:

                if(exactSearch) {
                    return capRepository.findByDocenaPrice(priceSearchRequest.getExactPrice(), pageable).map(CapDTO::new);
                }
                
                return capRepository.findByDocenaPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(CapDTO::new);
    
            case UNIDAD:
                if(exactSearch) {
                    return capRepository.findByUnidadPrice(priceSearchRequest.getExactPrice(), pageable).map(CapDTO::new);
                }
                
                return capRepository.findByUnidadPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(CapDTO::new);
      
            case PACA:
                if(exactSearch) {
                    return capRepository.findByPacaPrice(priceSearchRequest.getExactPrice(), pageable).map(CapDTO::new);
                }
                return capRepository.findByPacaPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(CapDTO::new);

        

            default:
                throw new IllegalArgumentException("Tipo de trato de precio no soportado.");
    
        }

    }



}
