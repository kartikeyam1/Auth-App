package com.authapp.backend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Role enum.
 * Tests Lombok-generated methods and enum functionality.
 */
@DisplayName("Role Enum Tests")
class RoleTest {

    @Test
    @DisplayName("Should have correct role values")
    void shouldHaveCorrectRoleValues() {
        // Given & When & Then
        assertThat(Role.values()).hasSize(2);
        assertThat(Role.values()).containsExactlyInAnyOrder(Role.ROLE_USER, Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Should have correct display names")
    void shouldHaveCorrectDisplayNames() {
        // Given & When & Then
        assertThat(Role.ROLE_USER.getDisplayName()).isEqualTo("User");
        assertThat(Role.ROLE_ADMIN.getDisplayName()).isEqualTo("Administrator");
    }

    @Test
    @DisplayName("Should return role name in toString")
    void shouldReturnRoleNameInToString() {
        // Given & When & Then
        assertThat(Role.ROLE_USER.toString()).isEqualTo("ROLE_USER");
        assertThat(Role.ROLE_ADMIN.toString()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should be comparable")
    void shouldBeComparable() {
        // Given & When & Then
        assertThat(Role.ROLE_USER).isEqualTo(Role.ROLE_USER);
        assertThat(Role.ROLE_ADMIN).isEqualTo(Role.ROLE_ADMIN);
        assertThat(Role.ROLE_USER).isNotEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Should parse from string")
    void shouldParseFromString() {
        // Given & When & Then
        assertThat(Role.valueOf("ROLE_USER")).isEqualTo(Role.ROLE_USER);
        assertThat(Role.valueOf("ROLE_ADMIN")).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Should throw exception for invalid role")
    void shouldThrowExceptionForInvalidRole() {
        // Given & When & Then
        assertThatThrownBy(() -> Role.valueOf("INVALID_ROLE"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
