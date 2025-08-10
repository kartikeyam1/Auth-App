package com.authapp.backend.service;

import com.authapp.backend.dto.ChangePasswordRequestDto;
import com.authapp.backend.dto.LoginRequestDto;
import com.authapp.backend.dto.LoginResponseDto;
import com.authapp.backend.entity.User;
import com.authapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    
    /**
     * Authenticate user with email and password.
     * Note: This is basic authentication without encryption for demo purposes.
     * In production, passwords should be encrypted with BCrypt.
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        
        try {
            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
            
            if (userOptional.isEmpty()) {
                log.warn("Login failed: User not found for email: {}", loginRequest.getEmail());
                return LoginResponseDto.builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
            }
            
            User user = userOptional.get();
            
            // Check if user is enabled
            if (!user.getEnabled()) {
                log.warn("Login failed: User account is disabled for email: {}", loginRequest.getEmail());
                return LoginResponseDto.builder()
                    .success(false)
                    .message("Account is disabled")
                    .build();
            }
            
            // Verify password (plain text comparison for demo - use BCrypt in production)
            if (!loginRequest.getPassword().equals(user.getPassword())) {
                log.warn("Login failed: Invalid password for email: {}", loginRequest.getEmail());
                return LoginResponseDto.builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
            }
            
            // Update last login (we'll add this field to User entity)
            // For now, we'll just log it
            log.info("Login successful for user: {}", user.getEmail());
            
            // Create session info (basic session without Redis/database storage)
            String sessionId = UUID.randomUUID().toString();
            LocalDateTime sessionExpiry = LocalDateTime.now().plusHours(24); // 24 hour session
            
            return LoginResponseDto.builder()
                .success(true)
                .message("Login successful")
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .enabled(user.getEnabled())
                .lastLogin(LocalDateTime.now())
                .sessionId(sessionId)
                .sessionExpiry(sessionExpiry)
                .build();
                
        } catch (Exception e) {
            log.error("Login error for email: {}", loginRequest.getEmail(), e);
            return LoginResponseDto.builder()
                .success(false)
                .message("Login failed due to server error")
                .build();
        }
    }
    
    /**
     * Change user password.
     * Note: This is basic password change without encryption for demo purposes.
     */
    @Transactional
    public boolean changePassword(ChangePasswordRequestDto changePasswordRequest) {
        log.info("Password change attempt for email: {}", changePasswordRequest.getEmail());
        
        try {
            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(changePasswordRequest.getEmail());
            
            if (userOptional.isEmpty()) {
                log.warn("Password change failed: User not found for email: {}", changePasswordRequest.getEmail());
                return false;
            }
            
            User user = userOptional.get();
            
            // Check if user is enabled
            if (!user.getEnabled()) {
                log.warn("Password change failed: User account is disabled for email: {}", changePasswordRequest.getEmail());
                return false;
            }
            
            // Verify current password (plain text comparison for demo)
            if (!changePasswordRequest.getCurrentPassword().equals(user.getPassword())) {
                log.warn("Password change failed: Invalid current password for email: {}", changePasswordRequest.getEmail());
                return false;
            }
            
            // Update password (plain text for demo - use BCrypt in production)
            user.setPassword(changePasswordRequest.getNewPassword());
            userRepository.save(user);
            
            log.info("Password changed successfully for user: {}", user.getEmail());
            return true;
            
        } catch (Exception e) {
            log.error("Password change error for email: {}", changePasswordRequest.getEmail(), e);
            return false;
        }
    }
    
    /**
     * Validate session (basic validation without database storage).
     * In production, this would check a session store (Redis, database, etc.)
     */
    public boolean validateSession(String sessionId) {
        // Basic validation - in production this would check against a session store
        return sessionId != null && !sessionId.trim().isEmpty();
    }
    
    /**
     * Logout user (invalidate session).
     * In production, this would remove the session from the session store.
     */
    public boolean logout(String sessionId) {
        log.info("Logout for session: {}", sessionId);
        // In production, remove session from store
        return true;
    }
}
