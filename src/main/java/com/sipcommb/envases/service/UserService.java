package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.LoginResponse;
import com.sipcommb.envases.dto.UserDTO;
import com.sipcommb.envases.dto.UserRequestDTO;
import com.sipcommb.envases.dto.UserResponseDTO;
import com.sipcommb.envases.entity.Role;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.RoleRepository;
import com.sipcommb.envases.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

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
     * Change user password
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("no se encontró el usuario");
        }

        User user = userOptional.get();

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
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


    // FUNCIONALIDADES DE LOGIN Y AUTENTICACIÓN

    /**
     *  Login user with username and password
     */
    public LoginResponse login(String username, String password) {
        if(!userFound(username)) {
            throw new BadCredentialsException("El usuario no existe");
        }
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.get();
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        if(!user.getIsActive()) {
            throw new BadCredentialsException("El usuario está desactivado");
        }

        user.setLastLogin(LocalDateTime.now());
        String token = jwtService.generateToken(user);
        return new LoginResponse(
            token,
            user.getUsername(),
            user.getRole().getName(),
            user.getRole().getPermissions()
        ); 
    }

    /**
     * Register new user
     */
    public UserDTO register(UserRequestDTO userRequestDTO) {
        // Check if username already exists
        if (userFound(userRequestDTO.getUsername())) {
            throw new RuntimeException("Ya existe el usuario");
        }
        // Check if email already exists
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Find role
        Optional<Role> roleOptional = roleRepository.findByName(userRequestDTO.getRoleName());
        if (!roleOptional.isPresent()) {
            throw new RuntimeException("Role not found: " + userRequestDTO.getRoleName());
        }

        if(roleOptional.get().getName().toLowerCase().equals("admin")) {
            throw new RuntimeException("Solo un administrador puede crear usuarios con el rol de administrador");
        }   

        // Create user
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        user.setRole(roleOptional.get());
        user.setIsActive(true);
        // Save user
        user = userRepository.save(user);
        return new UserDTO(user);
    }
       

    private boolean userFound(String userName) {
        return userRepository.existsByUsername(userName);
    }

    public UserDTO registerAdmin(UserRequestDTO userRequestDTO, String authHeader) {
        if (!jwtService.getRoleFromToken(authHeader).equals("admin")) {
            throw new RuntimeException("Este usuario no tiene permiso para crear administradores");
        }

        // Check if username already exists
        if (userFound(userRequestDTO.getUsername())) {
            throw new RuntimeException("Ya existe el usuario");
        }
        // Check if email already exists
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new RuntimeException("ya existe un usuario con este email");
        }

        // Find role
        Optional<Role> roleOptional = roleRepository.findByName(userRequestDTO.getRoleName());
        if (!roleOptional.isPresent()) {
            throw new RuntimeException("Role not found: " + userRequestDTO.getRoleName());
        }

        // Create user
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        user.setRole(roleOptional.get());
        user.setIsActive(true);
        
        // Save user
        user = userRepository.save(user);
        
        return new UserDTO(user);
    }

    public UserDTO updateUser(UserRequestDTO userRequestDTO){
        Optional<User> userOptional = userRepository.findByUsername(userRequestDTO.getUsername());
        if (!userOptional.isPresent()) {
            throw new RuntimeException("no se encontró el usuario");
        }

        User user = userOptional.get();

        // Check if email is being updated and if it already exists
        if (!user.getEmail().equals(userRequestDTO.getEmail()) && userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new RuntimeException("ya existe un usuario con este email");
        }

        // Find role
        Optional<Role> roleOptional = roleRepository.findByName(userRequestDTO.getRoleName());
        if (!roleOptional.isPresent()) {
            throw new RuntimeException("No se encontro el rol: " + userRequestDTO.getRoleName());
        }

        // Update user details
        user.setEmail(userRequestDTO.getEmail());
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        user.setRole(roleOptional.get());

        // Save updated user
        user = userRepository.save(user);
        return new UserDTO(user);
    }


    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserResponseDTO::new);
    }

    public Page<UserResponseDTO> getUsersByRole(Pageable pageable, String roleName) {
        Page<User> users = userRepository.findByRoleName(roleName, pageable);
        return users.map(UserResponseDTO::new);
    }

    public Page<UserResponseDTO> getByName(Pageable pageable, String name) {
        Page<User> users = userRepository.findByName(name, pageable);
        return users.map(UserResponseDTO::new);
    }

    public UserResponseDTO deActivateUser(String username){
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("no se encontró el usuario");
        }

        User user = userOptional.get();
        user.setIsActive(false);
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    public UserResponseDTO activateUser(String username){
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("no se encontró el usuario");
        }

        User user = userOptional.get();
        user.setIsActive(true);
        userRepository.save(user);
        return new UserResponseDTO(user);
    }


}
