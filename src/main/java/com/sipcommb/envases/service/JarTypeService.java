package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.JarTypeDTO;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.JarTypeRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class JarTypeService {

    @Autowired
    private JarTypeRepository jarTypeRepository;

    @Autowired
    private JarRepository jarRepository;

    @Autowired
    private CapRepository capRepository;

    public Optional<JarType> getJarTypeByName(String name) {
        return jarTypeRepository.getTypeByName(name);
    }

    public Optional<JarType> getJarTypeByDiameter(String diameter) {
        return jarTypeRepository.getTypeByDiameter(diameter);
    }

    public JarTypeDTO addJarTypes(JarTypeDTO jarTypeDTO) {
        JarType jarType = new JarType();
        String diameter = jarTypeDTO.getDiameter().trim().toLowerCase();
        // Assuming diameter is a unique identifier for JarType
        if (jarTypeRepository.getTypeByDiameter(diameter).isPresent()) {
            throw new RuntimeException("Ya existe este diametro: " + diameter);
        }

        if (jarTypeRepository.getTypeByName(jarTypeDTO.getName()).isPresent()) {
            throw new RuntimeException("Ya existe un tipo de tapa con el nombre: " + jarTypeDTO.getName());
        }

        jarType.setName(jarTypeDTO.getName());
        jarType.setDescription(jarTypeDTO.getDescription());
        jarType.setDiameter(diameter);
        jarType.setCreatedAt(java.time.LocalDateTime.now());
        jarTypeRepository.save(jarType);

        return jarTypeDTO;
    }

    public JarType addJarTypeExcel(JarTypeDTO jarTypeDTO){
         JarType jarType = new JarType();
        String diameter = jarTypeDTO.getDiameter().trim().toLowerCase();
        // Assuming diameter is a unique identifier for JarType
        if (jarTypeRepository.getTypeByDiameter(diameter).isPresent()) {
            throw new RuntimeException("Ya existe este diametro: " + diameter);
        }

        if (jarTypeRepository.getTypeByName(jarTypeDTO.getName()).isPresent()) {
            throw new RuntimeException("Ya existe un tipo de tapa con el nombre: " + jarTypeDTO.getName());
        }

        jarType.setName(jarTypeDTO.getName());
        jarType.setDescription(jarTypeDTO.getDescription());
        jarType.setDiameter(diameter);
        jarType.setCreatedAt(java.time.LocalDateTime.now());
        return jarType;
    }

    public JarTypeDTO updateJarType(String diameter, JarTypeDTO jarTypeDTO) {
        Optional<JarType> jarTypeOpt = jarTypeRepository.getTypeByDiameter(diameter);

        if (!jarTypeOpt.isPresent()) {
            throw new RuntimeException("No se encontro el tipo de tapa con diametro: " + diameter);
        }

        JarType jarType = jarTypeOpt.get();

        if (jarTypeRepository.getTypeByName(jarTypeDTO.getName()).isPresent() &&
                !jarType.getName().equals(jarTypeDTO.getName())) {
            throw new RuntimeException("Ya existe un tipo de tapa con el nombre: " + jarTypeDTO.getName());
        }

        jarType.setName(jarTypeDTO.getName());
        jarType.setDescription(jarTypeDTO.getDescription());
        jarTypeRepository.save(jarType);
        return jarTypeDTO;
    }

    public JarTypeDTO getByDiameter(String diameter) {
        Optional<JarType> jarType = jarTypeRepository.getTypeByDiameter(diameter);
        if (jarType.isPresent()) {
            JarTypeDTO jarTypeDTO = new JarTypeDTO();
            jarTypeDTO.setName(jarType.get().getName());
            jarTypeDTO.setDescription(jarType.get().getDescription());
            jarTypeDTO.setDiameter(jarType.get().getDiameter());
            return jarTypeDTO;
        } else {
            throw new RuntimeException("No se encontro el tipo de tapa con diametro: " + diameter);
        }
    }

    public Page<JarTypeDTO> getAll(Pageable pageable) {
        Page<JarType> jarTypes = jarTypeRepository.findAll(pageable);
        return jarTypes.map(JarTypeDTO::new);
    }

    public Page<JarTypeDTO> getLikeName(Pageable pageable, String name) {
        Page<JarType> jarTypes = jarTypeRepository.findLikeName(name, pageable);
        return jarTypes.map(JarTypeDTO::new);
    }

    public List<JarTypeDTO> getAll() {
        List<JarType> jarTypes = jarTypeRepository.findAll();
        return jarTypes.stream()
                .map(JarTypeDTO::new)
                .collect(Collectors.toList());
    }

    public JarTypeDTO setActive(String diameter, Boolean isActive) {
        Optional<JarType> jarTypeOpt = jarTypeRepository.getTypeByDiameter(diameter);

        if (!jarTypeOpt.isPresent()) {
            throw new RuntimeException("No se encontro el tipo de tapa con diametro: " + diameter);
        }

        JarType jarType = jarTypeOpt.get();
        jarType.setIsActive(isActive);

        if (!isActive) {
            List<Jar> jars = jarType.getJars();
            if (jars != null && !jars.isEmpty()) {
                for (Jar jar : jars) {
                    jar.setIsActive(false);
                    jarRepository.save(jar);
                }
            }
            List<Cap> caps = jarType.getCaps();
            if (caps != null && !caps.isEmpty()) {
                for (Cap cap : caps) {
                    cap.setIsActive(false);
                    capRepository.save(cap);
                }
            }
        }

        jarTypeRepository.save(jarType);

        return new JarTypeDTO(jarType);
    }

}
