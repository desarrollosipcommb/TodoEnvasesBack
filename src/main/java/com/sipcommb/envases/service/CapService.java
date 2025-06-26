package com.sipcommb.envases.service;

import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.CapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CapService {

    @Autowired
    private CapRepository capRepository;

    /**
     * Get all active caps
     */
    public List<Cap> getAllActiveCaps() {
        return capRepository.findByIsActiveTrue();
    }

    /**
     * Get cap by ID
     */
    public Optional<Cap> getCapById(Long id) {
        return capRepository.findById(id);
    }

    /**
     * Create new cap
     */
    public Cap createCap(String name, String description, String color, JarType jarType, Integer quantity, BigDecimal unitPrice) {
        // Check if cap name already exists
        if (capRepository.existsByName(name)) {
            throw new RuntimeException("Cap name already exists");
        }

        Cap cap = new Cap();
        cap.setName(name);
        cap.setDescription(description);
        cap.setColor(color);
        cap.setJarType(jarType);
        cap.setQuantity(quantity);
        cap.setUnitPrice(unitPrice);
        cap.setIsActive(true);

        return capRepository.save(cap);
    }

    /**
     * Update cap information
     */
    public Cap updateCap(Long capId, String name, String description, String color, Integer quantity, BigDecimal unitPrice) {
        Optional<Cap> capOptional = capRepository.findById(capId);
        if (capOptional.isEmpty()) {
            throw new RuntimeException("Cap not found");
        }

        Cap cap = capOptional.get();
        
        // Check if new name is already taken by another cap
        if (!cap.getName().equals(name) && capRepository.existsByName(name)) {
            throw new RuntimeException("Cap name already exists");
        }

        cap.setName(name);
        cap.setDescription(description);
        cap.setColor(color);
        cap.setQuantity(quantity);
        cap.setUnitPrice(unitPrice);

        return capRepository.save(cap);
    }

    /**
     * Update cap stock
     */
    public Cap updateCapStock(Long capId, Integer newQuantity) {
        Optional<Cap> capOptional = capRepository.findById(capId);
        if (capOptional.isEmpty()) {
            throw new RuntimeException("Cap not found");
        }

        Cap cap = capOptional.get();
        cap.setQuantity(newQuantity);
        return capRepository.save(cap);
    }

    /**
     * Get caps with low stock
     */
    public List<Cap> getLowStockCaps(Integer threshold) {
        return capRepository.findLowStockCaps(threshold);
    }

    /**
     * Get caps by jar type (compatibility)
     */
    public List<Cap> getCapsByJarType(JarType jarType) {
        return capRepository.findByJarTypeAndIsActiveTrue(jarType);
    }

    /**
     * Get caps by color
     */
    public List<Cap> getCapsByColor(String color) {
        return capRepository.findByColorAndIsActiveTrue(color);
    }

    /**
     * Get available cap colors
     */
    public List<String> getAvailableCapColors() {
        return capRepository.findDistinctColors();
    }

    /**
     * Deactivate cap (soft delete)
     */
    public void deactivateCap(Long capId) {
        Optional<Cap> capOptional = capRepository.findById(capId);
        if (capOptional.isEmpty()) {
            throw new RuntimeException("Cap not found");
        }

        Cap cap = capOptional.get();
        cap.setIsActive(false);
        capRepository.save(cap);
    }

    /**
     * Search caps by name
     */
    public Optional<Cap> getCapByName(String name) {
        return capRepository.findByName(name);
    }

    /**
     * Get caps within price range
     */
    public List<Cap> getCapsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return capRepository.findByPriceRange(minPrice, maxPrice);
    }

    /**
     * Check if cap name exists
     */
    public boolean capNameExists(String name) {
        return capRepository.existsByName(name);
    }
}
