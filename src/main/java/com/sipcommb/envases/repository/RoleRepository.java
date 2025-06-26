package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Find role by name
    Optional<Role> findByName(String name);
    
    // Check if role name already exists
    boolean existsByName(String name);
}
