package com.authapp.backend.dto;

import com.authapp.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for login responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    
    private Long id;
    private String email;
    private Set<Role> roles;
    private boolean enabled;
    private LocalDateTime lastLogin;
    private String message;
    private boolean success;
    
    // Session info (for basic auth without JWT)
    private String sessionId;
    private LocalDateTime sessionExpiry;
}
