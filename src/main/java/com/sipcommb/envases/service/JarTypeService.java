package com.sipcommb.envases.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.JarTypeDTO;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.JarTypeRepository;

@Service
public class JarTypeService {
    
    @Autowired
    private JarTypeRepository jarTypeRepository;

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

        jarType.setName(jarTypeDTO.getName());
        jarType.setDescription(jarTypeDTO.getDescription());
        jarType.setDiameter(diameter);
        jarType.setCreatedAt(java.time.LocalDateTime.now());
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

    public List<JarTypeDTO> getAll(){
        List<JarType> jarTypes = jarTypeRepository.findAll();
        List<JarTypeDTO> jarTypeDTOs = new ArrayList<>();
        for (JarType jarType : jarTypes) {
            JarTypeDTO jarTypeDTO = new JarTypeDTO();
            jarTypeDTO.setName(jarType.getName());
            jarTypeDTO.setDescription(jarType.getDescription());
            jarTypeDTO.setDiameter(jarType.getDiameter());
            jarTypeDTOs.add(jarTypeDTO);
        }
        return jarTypeDTOs;
    }

}
