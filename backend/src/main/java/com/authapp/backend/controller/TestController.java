package com.authapp.backend.controller;

import com.authapp.backend.dto.CreateUserDto;
import com.authapp.backend.dto.UserResponseDto;
import com.authapp.backend.entity.Role;
import com.authapp.backend.entity.User;
import com.authapp.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Test controller to verify H2 database connectivity and basic CRUD operations.
 * This controller will be replaced with proper authentication controllers in Part 3.
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class TestController {

    private final UserService userService;

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.info("Health check requested");
        
        Map<String, Object> response = Map.of(
            "status", "UP",
            "message", "Auth Backend is running",
            "timestamp", System.currentTimeMillis(),
            "database", "H2 Connected",
            "totalUsers", userService.getTotalUserCount()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create a test user.
     */
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        log.info("Creating test user with email: {}", createUserDto.getEmail());
        
        try {
            User user = User.builder()
                    .email(createUserDto.getEmail())
                    .password(createUserDto.getPassword()) // Note: In Part 3, this will be encoded
                    .build();
            
            // Add roles
            if (createUserDto.getRoles() != null && !createUserDto.getRoles().isEmpty()) {
                createUserDto.getRoles().forEach(user::addRole);
            } else {
                user.addRole(Role.ROLE_USER); // Default role
            }
            
            User savedUser = userService.createUser(user);
            
            UserResponseDto response = mapToResponseDto(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    /**
     * Get all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        log.info("Retrieving all users");
        
        List<User> users = userService.findAllUsers();
        List<UserResponseDto> response = users.stream()
                .map(this::mapToResponseDto)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("Retrieving user by ID: {}", id);
        
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            UserResponseDto response = mapToResponseDto(user.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by email.
     */
    @GetMapping("/users/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        log.info("Retrieving user by email: {}", email);
        
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            UserResponseDto response = mapToResponseDto(user.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get users by role.
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable Role role) {
        log.info("Retrieving users by role: {}", role);
        
        List<User> users = userService.findByRole(role);
        List<UserResponseDto> response = users.stream()
                .map(this::mapToResponseDto)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user by ID.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        log.info("Deleting user by ID: {}", id);
        
        try {
            userService.deleteUser(id);
            Map<String, String> response = Map.of("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            Map<String, String> response = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get database statistics.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Retrieving database statistics");
        
        Map<String, Object> stats = Map.of(
            "totalUsers", userService.getTotalUserCount(),
            "adminUsers", userService.getUserCountByRole(Role.ROLE_ADMIN),
            "regularUsers", userService.getUserCountByRole(Role.ROLE_USER)
        );
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Initialize sample data.
     */
    @PostMapping("/init-data")
    public ResponseEntity<Map<String, String>> initSampleData() {
        log.info("Initializing sample data");
        
        try {
            // Create admin user
            if (!userService.existsByEmail("admin@authapp.com")) {
                User admin = User.createAdmin("admin@authapp.com", "admin123");
                userService.createUser(admin);
            }
            
            // Create regular user
            if (!userService.existsByEmail("user@authapp.com")) {
                User user = User.createUser("user@authapp.com", "user123");
                userService.createUser(user);
            }
            
            Map<String, String> response = Map.of("message", "Sample data initialized successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error initializing sample data: {}", e.getMessage());
            Map<String, String> response = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Map User entity to UserResponseDto.
     */
    private UserResponseDto mapToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .enabled(user.getEnabled())
                .accountNonExpired(user.getAccountNonExpired())
                .accountNonLocked(user.getAccountNonLocked())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
