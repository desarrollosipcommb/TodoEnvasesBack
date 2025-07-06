package com.sipcommb.envases.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.JarTypeRepository;

@Service
public class JarTypeService {
    
    @Autowired
    private JarTypeRepository jarTypeRepository;

    public Optional<JarType> getJarTypeByName(String name) {
        return jarTypeRepository.getTypeByName(name);
    }


}
