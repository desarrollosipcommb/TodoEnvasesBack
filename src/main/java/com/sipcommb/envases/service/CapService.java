package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.CapColorDTO;
import com.sipcommb.envases.dto.CapColorRequest;
import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.CapColor;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.JarTypeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CapService {

    @Autowired
    private CapRepository capRepository;

    @Autowired
    private JarTypeRepository jarTypeRepository;



    @Autowired
    private CapColorService capColorService;



    public CapDTO addCaps(CapRequest capRequest, String token) {

        if (capRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la tapa no puede estar vacío.");
        }

        if (capRequest.getDiameter().isEmpty()) {
            capRequest.setDiameter("");
        }

        if (capRepository.findByNameAndDiameter(capRequest.getName(), capRequest.getDiameter()).isPresent()) {
            throw new RuntimeException("Ya existe una tapa con el mismo nombre y diametro");
        }

        Cap cap = new Cap();

        if (!capRequest.getDiameter().equals("") && !jarTypeRepository.getTypeByDiameter(capRequest.getDiameter()).isPresent()) {
            throw new RuntimeException("No existe un tipo de frasco con el diámetro especificado.");
        } else {
            JarType jarType = jarTypeRepository.getTypeByDiameter(capRequest.getDiameter()).get();
            cap.setJarType(jarType);
        }


        cap.setName(capRequest.getName().toLowerCase().trim());
        cap.setDescription(capRequest.getDescription());
        cap = capRepository.save(cap);

        CapDTO capDTO = new CapDTO(cap);

        return capDTO;
    }

    //TODO: probar.

    public CapDTO addCapColor(CapColorRequest capColorRequest, String token) {
        Optional<Cap> capOptional = capRepository.findByNameAndDiameter(capColorRequest.getName(), capColorRequest.getDiameter());
        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones con el nombre " + capColorRequest.getName() + ".");
        }
        Cap cap = capOptional.get();
        cap = capColorService.addCapColor(cap, capColorRequest, token);
        capRepository.save(cap);
        return new CapDTO(cap);

    }

    public Page<CapDTO> getAllCaps(Pageable pageable) {
        Page<Cap> caps = capRepository.findAll(pageable);
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getAllActiveCaps(Pageable pageable) {
        Page<Cap> caps = capRepository.findAllByIsActiveTrue(pageable);
        return caps.map(CapDTO::new);
    }

    public Page<CapColorDTO> getAllCapColor(CapRequest capRequest, String color, Pageable pageable) {
        Optional<Cap> capOptional = capRepository.findByNameAndDiameterIncludingInactive(capRequest.getName(), capRequest.getDiameter());
        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones con el nombre " + capRequest.getName() + ".");
        }
        Cap cap = capOptional.get();
       
        Page<CapColorDTO> capColors = capColorService.getAllCapColors(cap, color, pageable);
        return capColors;
    }

    public Page<CapColorDTO> getAllActiveCapColor(CapRequest capRequest, String color, Pageable pageable) {
        Optional<Cap> capOptional = capRepository.findByNameAndDiameter(capRequest.getName(), capRequest.getDiameter());
        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones con el nombre " + capRequest.getName() + ".");
        }
        Cap cap = capOptional.get();
        Page<CapColorDTO> capColors = capColorService.getAllCapColorsActive(cap, color, pageable);
        return capColors;
    }

    public Page<CapDTO> getAllInactiveCaps(Pageable pageable) {
        Page<Cap> caps = capRepository.findAllByIsActiveFalse(pageable);
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getCapsByDiameter(String diameter, Pageable pageable) {
        Page<Cap> caps = capRepository.getFromCapDiameter(diameter, pageable).get();
        if (caps.isEmpty()) {
            throw new RuntimeException("No existen tapas con el diámetro especificado.");
        }
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getByName(String name, Pageable pageable) {
        Page<Cap> caps = capRepository.getFromNameLike(name, pageable).get();
        if (caps.isEmpty()) {
            throw new RuntimeException("No existen tapas con el nombre especificado.");
        }
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getFromNameLikeAndNameDiameter(String name, String diameter, Pageable pageable) {
        Page<Cap> caps = capRepository.getFromNameLikeAndDiameter(name, diameter, pageable);
        return caps.map(CapDTO::new);
    }

    public Page<CapDTO> getFromNameLikeAndNameDiameterActive(String name, String diameter, Pageable pageable) {
        Page<Cap> caps = capRepository.getFromNameLikeAndDiameter(name, diameter, pageable);
        return caps.map(CapDTO::new);
    }


    public CapDTO updateCap(CapRequest capRequest, String token) {

        Optional<Cap> capOptional = capRepository.findByNameAndDiameter(capRequest.getName(), capRequest.getDiameter());

        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }

        Cap cap = capOptional.get();

        if (capRequest.getDescription() != null && !capRequest.getDescription().isEmpty()) {
            cap.setDescription(capRequest.getDescription());
        }

        cap.setDescription(capRequest.getDescription());
        cap.setUpdatedAt(LocalDateTime.now());
        capRepository.save(cap);


        return new CapDTO(cap);
    }

    public CapDTO updateColorCap(CapColorRequest capColorRequest, String token) {

        Optional<Cap> capOptional = capRepository.findByNameAndDiameter(capColorRequest.getName(), capColorRequest.getDiameter());
        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones: " + capColorRequest.getName() + " - " + capColorRequest.getDiameter());
        }
        Cap cap = capOptional.get();

        capColorService.updateCapColor(cap, capColorRequest);

        return new CapDTO(capRepository.save(cap));
    }

    public CapDTO deleteCap(CapRequest capRequest) {

        Optional<Cap> capOptional = capRepository.findByNameAndDiameter(capRequest.getName(), capRequest.getDiameter());
        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }
        Cap cap = capOptional.get();
        cap.setUpdatedAt(LocalDateTime.now());
        cap.setIsActive(false);

        List<CapColor> activeColors = cap.getColors();
        for (CapColor capColor : activeColors) {
            capColorService.deactivateCapColor(capColor);
        }
        capRepository.save(cap);
        return new CapDTO(cap);
    }

    public CapDTO activateCap(CapRequest capRequest) {

        Optional<Cap> capOptional = capRepository.findByNameAndDiameterIncludingInactive(capRequest.getName(), capRequest.getDiameter());
        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }

        Cap cap = capOptional.get();

        if(cap.getJarType().getIsActive() == false) {
            throw new RuntimeException("No se puede activar la tapa porque el tipo de frasco asociado está inactivo.");
        }
        cap.setIsActive(true);
        cap.setUpdatedAt(LocalDateTime.now());

        List<CapColor> inactiveColors = cap.getColors();
        for (CapColor capColor : inactiveColors) {
            capColorService.activateCapColor(capColor);
        }

        capRepository.save(cap);
        return new CapDTO(cap);
    }

    public CapDTO updateCapInventory(CapColorRequest capColorRequest, String token) {
        Optional<Cap> capOptional = capRepository.findByNameAndDiameter(capColorRequest.getName(), capColorRequest.getDiameter());
        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }
        Cap cap = capOptional.get();

        capColorService.updateCapColorInventory(cap, capColorRequest, token);
        capRepository.save(cap);

        return new CapDTO(cap);
    }

    public CapDTO addCapToBodega(CapColorRequest capColorRequest) {
        Optional<Cap> capOptional = capRepository.findByNameAndDiameter(capColorRequest.getName(), capColorRequest.getDiameter());
        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }

        Cap cap = capOptional.get();
        capColorService.addCapToBodega(cap, capColorRequest);
        capRepository.save(cap);
        return new CapDTO(cap);
    }


    public CapDTO changeInventory(CapColorRequest capColorRequest, String token) {
        Optional<Cap> capOptional = capRepository.findByNameAndDiameter(capColorRequest.getName(), capColorRequest.getDiameter());
        if (!capOptional.isPresent()) {
            throw new RuntimeException("No existe una tapa con estas especificaciones.");
        }

        Cap cap = capOptional.get();
        capColorService.updateCapColorInventory(cap, capColorRequest, token);
        capRepository.save(cap);
        return new CapDTO(cap);
    }

    public boolean existsByNameAndDiameter(String name, String diameter) {
        return capRepository.findByNameAndDiameter(name, diameter).isPresent();
    }

}
