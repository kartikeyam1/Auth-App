package com.authapp.backend.controller;

import com.authapp.backend.dto.ChangePasswordRequestDto;
import com.authapp.backend.dto.LoginRequestDto;
import com.authapp.backend.dto.LoginResponseDto;
import com.authapp.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * User login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("Login request received for email: {}", loginRequest.getEmail());
        
        try {
            LoginResponseDto response = authService.login(loginRequest);
            
            if (response.isSuccess()) {
                log.info("Login successful for user: {}", loginRequest.getEmail());
                return ResponseEntity.ok(response);
            } else {
                log.warn("Login failed for user: {}", loginRequest.getEmail());
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", response.getMessage()
                ));
            }
            
        } catch (Exception e) {
            log.error("Login error for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Login failed due to server error"
            ));
        }
    }
    
    /**
     * Change password endpoint.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequest) {
        log.info("Password change request received for email: {}", changePasswordRequest.getEmail());
        
        try {
            boolean success = authService.changePassword(changePasswordRequest);
            
            if (success) {
                log.info("Password change successful for user: {}", changePasswordRequest.getEmail());
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password changed successfully"
                ));
            } else {
                log.warn("Password change failed for user: {}", changePasswordRequest.getEmail());
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Password change failed. Please check your current password."
                ));
            }
            
        } catch (Exception e) {
            log.error("Password change error for email: {}", changePasswordRequest.getEmail(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Password change failed due to server error"
            ));
        }
    }
    
    /**
     * Logout endpoint.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "X-Session-ID", required = false) String sessionId) {
        log.info("Logout request received for session: {}", sessionId);
        
        try {
            boolean success = authService.logout(sessionId);
            
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", "Logged out successfully"
            ));
            
        } catch (Exception e) {
            log.error("Logout error for session: {}", sessionId, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Logout failed due to server error"
            ));
        }
    }
    
    /**
     * Validate session endpoint.
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(@RequestHeader(value = "X-Session-ID", required = false) String sessionId) {
        log.debug("Session validation request for session: {}", sessionId);
        
        try {
            boolean valid = authService.validateSession(sessionId);
            
            return ResponseEntity.ok(Map.of(
                "valid", valid,
                "sessionId", sessionId
            ));
            
        } catch (Exception e) {
            log.error("Session validation error for session: {}", sessionId, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "valid", false,
                "message", "Session validation failed due to server error"
            ));
        }
    }
}
