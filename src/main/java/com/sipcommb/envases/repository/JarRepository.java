package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.JarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface JarRepository extends JpaRepository<Jar, Long> {
    
    // Find jar by name
    Optional<Jar> findByName(String name);
    
    // Find all active jars
    List<Jar> findByIsActiveTrue();
    
    // Find jars by jar type
    List<Jar> findByJarType(JarType jarType);
    
    // Find jars by jar type and active status
    List<Jar> findByJarTypeAndIsActiveTrue(JarType jarType);
    
    // Find jars with low stock (quantity <= threshold)
    @Query("SELECT j FROM Jar j WHERE j.quantity <= :threshold AND j.isActive = true")
    List<Jar> findLowStockJars(@Param("threshold") Integer threshold);
    
    // Find jars within price range
    @Query("SELECT j FROM Jar j WHERE j.unitPrice BETWEEN :minPrice AND :maxPrice AND j.isActive = true")
    List<Jar> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Find jars by size
    List<Jar> findBySizeAndIsActiveTrue(String size);
    
    // Check if jar name already exists
    boolean existsByName(String name);
}
