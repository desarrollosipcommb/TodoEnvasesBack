package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.JarDTO;
import com.sipcommb.envases.dto.JarRequestDTO;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.JarCapCompatibility;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.JarCapCompatibilityRepository;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.JarTypeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class JarService {

    @Autowired
    private JarRepository jarRepository;

    @Autowired
    private JarTypeRepository jarTypeRepository;

    @Autowired
    private JarCapCompatibilityRepository jarCapCompatibilityRepository;

    @Autowired
    private CapRepository capRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JwtService jwtService;

    public JarDTO addJar(JarRequestDTO jarRequest, String token) {

        if(jarRepository.getByName(jarRequest.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un frasco con ese nombre.");
        }
        
        // hay que validar que el frasco si tenga un diametro asociado, dado que algunos vienen con tapa propia asi que el diametro no es obligatorio
        if(!jarRequest.getDiameter().isEmpty() && !jarTypeRepository.getTypeByDiameter(jarRequest.getDiameter()).isPresent()) {
            throw new IllegalArgumentException("No existe un tipo de frasco con ese diámetro.");
        }

        if(jarRequest.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        Jar jar = new Jar();

        if(!jarRequest.getDiameter().isEmpty()){
            jar.setJarType(jarTypeRepository.getTypeByDiameter(jarRequest.getDiameter()).get());
        }

        jar.setName(jarRequest.getName());
        jar.setDescription(jarRequest.getDescription());
        jar.setQuantity(jarRequest.getQuantity());
        jar.setUnitPrice(BigDecimal.valueOf(jarRequest.getUnitPrice()));
        jar.setDocenaPrice(BigDecimal.valueOf(jarRequest.getDocenaPrice()));
        jar.setCienPrice(BigDecimal.valueOf(jarRequest.getCienPrice()));
        jar.setPacaPrice(BigDecimal.valueOf(jarRequest.getPacaPrice()));
        jar.setUnitsInPaca(jarRequest.getUnitsInPaca());
        jarRepository.save(jar);

        manageCompatibility(jarRequest.getCompatibleCaps(), jarRequest.getUnCompatibleCaps(), jar);

        inventoryService.newItem(jar.getId(), "jar", jar.getQuantity().intValue(), "restock", jwtService.getUserIdFromToken(token).intValue(), "Se añadio "+jar.getName()+" al inventario");

        return new JarDTO(jar);
    }

    //Re visa que si tenga un tipo de frasco asociado, dado que algunos vienen con tapa propia asi que el diametro no es obligatorio
    private void manageCompatibility(String[] compatibleCaps, String[] unCompatibleCaps, Jar jar) {

        if(jar.getJarType() != null) {
           if(compatibleCaps != null && compatibleCaps.length > 0) {

                //se crean dos listas, una con todas las tapas compatibles y otra con todas las tapas del diametro del frasco, le restamos las tapas compatibles a las del diametro del frasco
                // y asi tenemos las tapas incompatibles
                List<Cap> compatible = getCaps(compatibleCaps, jar);
                List<Cap> inCompatible = capRepository.getFromCapDiameter(jar.getJarType().getDiameter()).get();
                inCompatible.removeAll(compatible);

                addToCompatible(compatible, jar, true);
                addToCompatible(inCompatible, jar, false);
                
            }else if(compatibleCaps != null && compatibleCaps.length > 0) {
                List<Cap> unCompatible = getCaps(unCompatibleCaps, jar);
                List<Cap> compatible = capRepository.getFromCapDiameter(jar.getJarType().getDiameter()).get();
                compatible.removeAll(unCompatible);

                addToCompatible(compatible, jar, true);
                addToCompatible(unCompatible, jar, false);
            }
        }
    }
    
    //Trae todas las tapas que coincidan con los nombres y el diametro del frasco
    private List<Cap> getCaps(String[] caps, Jar jar) {
        List<Cap> capList = new ArrayList<>();
        if(caps != null && caps.length > 0) {
            for (String capName : caps) {
                if(capRepository.getFromNameAndDiameter(capName, jar.getJarType().getDiameter()).isPresent()) {
                    capList.addAll(capRepository.getFromNameAndDiameter(capName, jar.getJarType().getDiameter()).get());
                }
            }
        }
        return capList;

    }

    //Agrega las tapas a la lista de compatibilidad del frasco, si es compatible o no
    private void addToCompatible(List<Cap> compatible, Jar jar, boolean isCompatible) {
        for (Cap cap : compatible) {
            jarCapCompatibilityRepository.save(new JarCapCompatibility(jar, cap, isCompatible));
        }
        
    }

    
    public List<JarDTO> getAllJars() {
        List<Jar> jars = jarRepository.findAll();
        List<JarDTO> jarDTOs = new ArrayList<>();
        for (Jar jar : jars) {
            jarDTOs.add(new JarDTO(jar));
        }
        return jarDTOs;
    }
    
}
