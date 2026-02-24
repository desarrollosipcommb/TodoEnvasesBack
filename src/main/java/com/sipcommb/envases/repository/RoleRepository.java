package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Find role by name
    Optional<Role> findByName(String name);
    
    // Check if role name already exists
    boolean existsByName(String name);
}
