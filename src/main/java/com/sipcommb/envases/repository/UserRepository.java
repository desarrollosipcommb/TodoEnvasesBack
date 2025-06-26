package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username (for authentication)
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if username already exists
    boolean existsByUsername(String username);
    
    // Check if email already exists
    boolean existsByEmail(String email);
    
    // Find all active users
    List<User> findByIsActiveTrue();
    
    // Find users by role name
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    // Find users by role and active status
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.isActive = :isActive")
    List<User> findByRoleNameAndIsActive(@Param("roleName") String roleName, @Param("isActive") Boolean isActive);
}
