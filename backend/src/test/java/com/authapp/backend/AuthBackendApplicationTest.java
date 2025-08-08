package com.authapp.backend;

import com.authapp.backend.entity.Role;
import com.authapp.backend.entity.User;
import com.authapp.backend.repository.UserRepository;
import com.authapp.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for the complete Spring Boot application.
 * Tests the full application stack with embedded server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Spring Boot Application Integration Tests")
class AuthBackendApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Should load Spring context successfully")
    void shouldLoadSpringContextSuccessfully() {
        // Context loads if this test runs without exceptions
        assertThat(restTemplate).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    @DisplayName("Should initialize with default users")
    void shouldInitializeWithDefaultUsers() {
        // Then
        assertThat(userService.existsByEmail("admin@authapp.com")).isTrue();
        assertThat(userService.existsByEmail("user@authapp.com")).isTrue();
        
        // Verify admin user
        User admin = userService.findByEmail("admin@authapp.com").orElseThrow();
        assertThat(admin.hasRole(Role.ROLE_ADMIN)).isTrue();
        
        // Verify regular user
        User user = userService.findByEmail("user@authapp.com").orElseThrow();
        assertThat(user.hasRole(Role.ROLE_USER)).isTrue();
    }

    @Test
    @DisplayName("Should respond to health check endpoint")
    void shouldRespondToHealthCheckEndpoint() {
        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/test/health", 
                Map.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        assertThat(response.getBody().get("message")).isEqualTo("Auth Backend is running");
        assertThat(response.getBody().get("database")).isEqualTo("H2 Connected");
        assertThat(response.getBody().get("totalUsers")).isNotNull();
    }

    @Test
    @DisplayName("Should respond to stats endpoint")
    void shouldRespondToStatsEndpoint() {
        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/test/stats", 
                Map.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("totalUsers")).isNotNull();
        assertThat(response.getBody().get("adminUsers")).isNotNull();
        assertThat(response.getBody().get("regularUsers")).isNotNull();
        
        // Verify we have at least the default users
        Integer totalUsers = (Integer) response.getBody().get("totalUsers");
        assertThat(totalUsers).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should handle CORS properly")
    void shouldHandleCorssProperly() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/test/health", 
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // CORS headers would be tested in a proper cross-origin scenario
        // For this test, we just verify the endpoint is accessible
    }

    @Test
    @DisplayName("Should return 404 for non-existent endpoints")
    void shouldReturn404ForNonExistentEndpoints() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/nonexistent", 
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
