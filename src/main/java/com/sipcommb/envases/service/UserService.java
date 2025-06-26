package com.sipcommb.envases.service;

import com.sipcommb.envases.entity.Role;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.RoleRepository;
import com.sipcommb.envases.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all active users
     */
    public List<User> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Create new user
     */
    public User createUser(String username, String email, String password, String firstName, String lastName, String roleName) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // Find role
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            throw new RuntimeException("Role not found: " + roleName);
        }

        // Create user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(roleOptional.get());
        user.setIsActive(true);

        return userRepository.save(user);
    }

    /**
     * Update user information
     */
    public User updateUser(Long userId, String firstName, String lastName, String email) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        
        // Check if new email is already taken by another user
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        return userRepository.save(user);
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Deactivate user (soft delete)
     */
    public void deactivateUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Activate user
     */
    public void activateUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        user.setIsActive(true);
        userRepository.save(user);
    }

    /**
     * Get users by role
     */
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    /**
     * Get active users by role
     */
    public List<User> getActiveUsersByRole(String roleName) {
        return userRepository.findByRoleNameAndIsActive(roleName, true);
    }
}
