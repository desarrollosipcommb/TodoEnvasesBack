package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.JarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JarTypeRepository extends JpaRepository<JarType, Long> {
    
    // Find jar type by name
    Optional<JarType> findByName(String name);
    
    // Check if jar type name already exists
    boolean existsByName(String name);
    
    // Find all jar types ordered by name
    List<JarType> findAllByOrderByNameAsc();
}
