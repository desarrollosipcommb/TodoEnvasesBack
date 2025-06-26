package com.sipcommb.envases.service;

import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.JarTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JarService {

    @Autowired
    private JarRepository jarRepository;

    @Autowired
    private JarTypeRepository jarTypeRepository;

    // ============ JAR OPERATIONS ============

    /**
     * Get all active jars
     */
    public List<Jar> getAllActiveJars() {
        return jarRepository.findByIsActiveTrue();
    }

    /**
     * Get jar by ID
     */
    public Optional<Jar> getJarById(Long id) {
        return jarRepository.findById(id);
    }

    /**
     * Create new jar
     */
    public Jar createJar(String name, String description, JarType jarType, Integer quantity, BigDecimal unitPrice) {
        // Check if jar name already exists
        if (jarRepository.existsByName(name)) {
            throw new RuntimeException("Jar name already exists");
        }

        Jar jar = new Jar();
        jar.setName(name);
        jar.setDescription(description);
        jar.setJarType(jarType);
        jar.setQuantity(quantity);
        jar.setUnitPrice(unitPrice);
        jar.setIsActive(true);

        return jarRepository.save(jar);
    }

    /**
     * Update jar information
     */
    public Jar updateJar(Long jarId, String name, String description, Integer quantity, BigDecimal unitPrice) {
        Optional<Jar> jarOptional = jarRepository.findById(jarId);
        if (jarOptional.isEmpty()) {
            throw new RuntimeException("Jar not found");
        }

        Jar jar = jarOptional.get();
        
        // Check if new name is already taken by another jar
        if (!jar.getName().equals(name) && jarRepository.existsByName(name)) {
            throw new RuntimeException("Jar name already exists");
        }

        jar.setName(name);
        jar.setDescription(description);
        jar.setQuantity(quantity);
        jar.setUnitPrice(unitPrice);

        return jarRepository.save(jar);
    }

    /**
     * Update jar stock
     */
    public Jar updateJarStock(Long jarId, Integer newQuantity) {
        Optional<Jar> jarOptional = jarRepository.findById(jarId);
        if (jarOptional.isEmpty()) {
            throw new RuntimeException("Jar not found");
        }

        Jar jar = jarOptional.get();
        jar.setQuantity(newQuantity);
        return jarRepository.save(jar);
    }

    /**
     * Get jars with low stock
     */
    public List<Jar> getLowStockJars(Integer threshold) {
        return jarRepository.findLowStockJars(threshold);
    }

    /**
     * Get jars by jar type
     */
    public List<Jar> getJarsByType(JarType jarType) {
        return jarRepository.findByJarTypeAndIsActiveTrue(jarType);
    }

    /**
     * Deactivate jar (soft delete)
     */
    public void deactivateJar(Long jarId) {
        Optional<Jar> jarOptional = jarRepository.findById(jarId);
        if (jarOptional.isEmpty()) {
            throw new RuntimeException("Jar not found");
        }

        Jar jar = jarOptional.get();
        jar.setIsActive(false);
        jarRepository.save(jar);
    }

    // ============ JAR TYPE OPERATIONS ============

    /**
     * Get all jar types
     */
    public List<JarType> getAllJarTypes() {
        return jarTypeRepository.findAllByOrderByNameAsc();
    }

    /**
     * Get jar type by ID
     */
    public Optional<JarType> getJarTypeById(Long id) {
        return jarTypeRepository.findById(id);
    }

    /**
     * Create new jar type
     */
    public JarType createJarType(String name, String description) {
        // Check if jar type name already exists
        if (jarTypeRepository.existsByName(name)) {
            throw new RuntimeException("Jar type name already exists");
        }

        JarType jarType = new JarType();
        jarType.setName(name);
        jarType.setDescription(description);

        return jarTypeRepository.save(jarType);
    }
}
