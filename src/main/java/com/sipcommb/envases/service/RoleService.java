package com.sipcommb.envases.service;

import com.sipcommb.envases.entity.Role;
import com.sipcommb.envases.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Get all roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Get role by ID
     */
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    /**
     * Get role by name
     */
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * Get permissions 
     */
    public Set<String> getPermissionsByRole(String roleName) {
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (!roleOptional.isPresent()) {
            throw new RuntimeException("No se encontro el rol: " + roleName);
        }
        Role role = roleOptional.get();
        return role.getPermissions(); // Assuming Role has a method to get permissions
    }

    /**
     * Create new role
     */
    public Role createRole(String name, String description) {
        // Check if role name already exists
        if (roleRepository.existsByName(name)) {
            throw new RuntimeException("Role name already exists");
        }

        Role role = new Role();
        role.setName(name);
        role.setDescription(description);

        return roleRepository.save(role);
    }

    /**
     * Update role
     */
    public Role updateRole(Long roleId, String name, String description) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (!roleOptional.isPresent()) {
            throw new RuntimeException("Role not found");
        }

        Role role = roleOptional.get();
        
        // Check if new name is already taken by another role
        if (!role.getName().equals(name) && roleRepository.existsByName(name)) {
            throw new RuntimeException("Role name already exists");
        }

        role.setName(name);
        role.setDescription(description);

        return roleRepository.save(role);
    }

    /**
     * Delete role
     */
    public void deleteRole(Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (!roleOptional.isPresent()) {
            throw new RuntimeException("Role not found");
        }

        roleRepository.deleteById(roleId);
    }
}
