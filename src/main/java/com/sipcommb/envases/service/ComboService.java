package com.sipcommb.envases.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.ComboRequest;
import com.sipcommb.envases.dto.ComboResponse;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.Combo;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.JarCapCompatibility;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.ComboRepository;
import com.sipcommb.envases.repository.JarCapCompatibilityRepository;
import com.sipcommb.envases.repository.JarRepository;

@Service
@Transactional
public class ComboService {

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private JarRepository jarRepository;

    @Autowired
    private CapRepository capRepository;

    @Autowired
    private JarCapCompatibilityRepository jarCapCompatibilityRepository;

    public ComboResponse addCombo(ComboRequest comboRequest) {

        if(comboRepository.findByName(comboRequest.getName().trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un combo con el nombre: " + comboRequest.getName() + ".");
        }

        Combo combo = new Combo();
        combo.setName(comboRequest.getName().trim());
       
        Optional<Jar> jarOpt = jarRepository.getByName(comboRequest.getJarName().trim());
        if(!jarOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un tarro con el nombre: " + comboRequest.getJarName() + ".");
        }else {
            combo.setJar(jarOpt.get());
        }

        Optional<Cap> capOpt = capRepository.findByNameAndDiameterAndColor(comboRequest.getCapName().trim(), comboRequest.getDiameter(), comboRequest.getColor());
        if(!capOpt.isPresent()) {
            throw new IllegalArgumentException("No existe una tapa con el nombre: " + comboRequest.getCapName() + ".");
        }else {
            combo.setCap(capOpt.get());
        }

        Optional<JarCapCompatibility> compatibilityOpt = jarCapCompatibilityRepository.findByJarAndCap(jarOpt.get().getId(), capOpt.get().getId());

        if(!compatibilityOpt.isPresent()) {
            throw new IllegalArgumentException("La tapa seleccionada no es compatible con el tarro seleccionado.");
        }else if(!compatibilityOpt.get().isCompatible()) {
            throw new IllegalArgumentException("Esta tapa no es compatible con el tarro seleccionado.");
        }

        if(comboRequest.getUnitPrice() == null || comboRequest.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser 0 o negativo.");
        }

        combo.setUnitPrice(comboRequest.getUnitPrice());

        if(comboRequest.getDocenaPrice() < 0) {
            throw new IllegalArgumentException("El precio por docena no puede ser negativo.");
        }

        combo.setDocenaPrice(comboRequest.getDocenaPrice() != null ? comboRequest.getDocenaPrice() : 0.00);

        if(comboRequest.getCienPrice() < 0) {
            throw new IllegalArgumentException("El precio por cien no puede ser negativo.");
        }

        combo.setCienPrice(comboRequest.getCienPrice() != null ? comboRequest.getCienPrice() : 0.00);

        combo.setDescription(comboRequest.getDescription() != null ? comboRequest.getDescription().trim() : "");

        combo.setCreatedAt(LocalDateTime.now());
        combo.setUpdatedAt(LocalDateTime.now());

        comboRepository.save(combo);
        
        return new ComboResponse(combo);
    }
    
    public List<ComboResponse> getAllCombos() {
        List<Combo> combos = comboRepository.findAll();
        return combos.stream().map(ComboResponse::new).collect(Collectors.toList());
    }

    public ComboResponse getByName(String comboName) {
        Optional<Combo> combo = comboRepository.findByName(comboName);
        if (!combo.isPresent()) {
            throw new IllegalArgumentException("No existe un combo con el nombre: " + comboName + ".");
        }
        return new ComboResponse(combo.get());
    }

    public ComboResponse updateCombo(ComboRequest comboRequest) {
        Optional<Combo> existingComboOpt = comboRepository.findByName(comboRequest.getName().trim());
        if (!existingComboOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un combo con el nombre: " + comboRequest.getName() + ".");
        }

        Combo existingCombo = existingComboOpt.get();

        if(comboRequest.getUnitPrice() != null && comboRequest.getUnitPrice() > 0) {
            existingCombo.setUnitPrice(comboRequest.getUnitPrice());
        }else if(comboRequest.getUnitPrice() <=0) {
            throw new IllegalArgumentException("El precio unitario no puede ser 0 o negativo.");
        }

        if(comboRequest.getDocenaPrice() != null && comboRequest.getDocenaPrice() > 0) {
            existingCombo.setDocenaPrice(comboRequest.getDocenaPrice());
        }else if(comboRequest.getDocenaPrice() < 0) {
            throw new IllegalArgumentException("El precio por docena no puede ser 0 o negativo.");
        }

        if(comboRequest.getCienPrice() != null && comboRequest.getCienPrice() > 0) {
            existingCombo.setCienPrice(comboRequest.getCienPrice());
        } else if(comboRequest.getCienPrice() < 0) {
            throw new IllegalArgumentException("El precio por cien no puede ser 0 o negativo.");
        }

        if(comboRequest.getDescription()!=null){
            existingCombo.setDescription(comboRequest.getDescription().trim());
        }

        existingCombo.setUpdatedAt(LocalDateTime.now());

        comboRepository.save(existingCombo);
        
        return new ComboResponse(existingCombo);
    }

    public ComboResponse activeCombo(String comboName) {
        Optional<Combo> existingComboOpt = comboRepository.findByName(comboName.trim());
        if (!existingComboOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un combo con el nombre: " + comboName + ".");
        }

        Combo existingCombo = existingComboOpt.get();
        existingCombo.setActive(true);
        existingCombo.setUpdatedAt(LocalDateTime.now());

        comboRepository.save(existingCombo);
        
        return new ComboResponse(existingCombo);
    }

    public ComboResponse deactivateCombo(String comboName) {
        Optional<Combo> existingComboOpt = comboRepository.findByName(comboName.trim());
        if (!existingComboOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un combo con el nombre: " + comboName + ".");
        }

        Combo existingCombo = existingComboOpt.get();
        existingCombo.setActive(false);
        existingCombo.setUpdatedAt(LocalDateTime.now());

        comboRepository.save(existingCombo);
        
        return new ComboResponse(existingCombo);
    }

    public List<ComboResponse> getAllActiveCombos() {
        List<Combo> activeCombos = comboRepository.findAllActiveCombos();
        return activeCombos.stream().map(ComboResponse::new).collect(Collectors.toList());
    }

    public List<ComboResponse> getAllInactiveCombos() {
        List<Combo> inactiveCombos = comboRepository.findAllInactiveCombos();
        return inactiveCombos.stream().map(ComboResponse::new).collect(Collectors.toList());
    }

}
