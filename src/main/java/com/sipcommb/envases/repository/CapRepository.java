package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CapRepository extends JpaRepository<Cap, Long> {
    
    // Find cap by name
    Optional<Cap> findByName(String name);
    
    // Find all active caps
    List<Cap> findByIsActiveTrue();
    
    // Find caps by jar type (compatibility)
    List<Cap> findByJarType(JarType jarType);
    
    // Find caps by jar type and active status
    List<Cap> findByJarTypeAndIsActiveTrue(JarType jarType);
    
    // Find caps by color
    List<Cap> findByColorAndIsActiveTrue(String color);
    
    // Find caps with low stock (quantity <= threshold)
    @Query("SELECT c FROM Cap c WHERE c.quantity <= :threshold AND c.isActive = true")
    List<Cap> findLowStockCaps(@Param("threshold") Integer threshold);
    
    // Find caps within price range
    @Query("SELECT c FROM Cap c WHERE c.unitPrice BETWEEN :minPrice AND :maxPrice AND c.isActive = true")
    List<Cap> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Check if cap name already exists
    boolean existsByName(String name);
    
    // Find available colors
    @Query("SELECT DISTINCT c.color FROM Cap c WHERE c.color IS NOT NULL AND c.isActive = true ORDER BY c.color")
    List<String> findDistinctColors();
}
