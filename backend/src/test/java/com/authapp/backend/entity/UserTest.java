package com.authapp.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for User entity.
 * Tests Lombok-generated methods, validation, and business logic.
 */
@DisplayName("User Entity Tests")
class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Lombok Generated Methods")
    class LombokTests {

        @Test
        @DisplayName("Should create user with builder pattern")
        void shouldCreateUserWithBuilder() {
            // Given & When
            User user = User.builder()
                    .email("test@example.com")
                    .password("password123")
                    .enabled(true)
                    .build();

            // Then
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getPassword()).isEqualTo("password123");
            assertThat(user.getEnabled()).isTrue();
            assertThat(user.getRoles()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Should use default values with builder")
        void shouldUseDefaultValuesWithBuilder() {
            // Given & When
            User user = User.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();

            // Then
            assertThat(user.getEnabled()).isTrue();
            assertThat(user.getAccountNonExpired()).isTrue();
            assertThat(user.getAccountNonLocked()).isTrue();
            assertThat(user.getCredentialsNonExpired()).isTrue();
            assertThat(user.getRoles()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Should create user with all args constructor")
        void shouldCreateUserWithAllArgsConstructor() {
            // Given & When
            User user = new User(
                    1L, "test@example.com", "password123", 
                    Set.of(Role.ROLE_USER), true, true, true, true, 
                    null, null
            );

            // Then
            assertThat(user.getId()).isEqualTo(1L);
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getRoles()).containsExactly(Role.ROLE_USER);
        }

        @Test
        @DisplayName("Should create user with no args constructor")
        void shouldCreateUserWithNoArgsConstructor() {
            // Given & When
            User user = new User();

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getId()).isNull();
            assertThat(user.getEmail()).isNull();
        }

        @Test
        @DisplayName("Should exclude password from toString")
        void shouldExcludePasswordFromToString() {
            // Given
            User user = User.builder()
                    .email("test@example.com")
                    .password("secretPassword123")
                    .build();

            // When
            String toString = user.toString();

            // Then
            assertThat(toString).contains("test@example.com");
            assertThat(toString).doesNotContain("secretPassword123");
        }

        @Test
        @DisplayName("Should implement equals and hashCode correctly")
        void shouldImplementEqualsAndHashCodeCorrectly() {
            // Given
            User user1 = User.builder().id(1L).email("test@example.com").build();
            User user2 = User.builder().id(1L).email("different@example.com").build();
            User user3 = User.builder().id(2L).email("test@example.com").build();

            // Then
            assertThat(user1).isEqualTo(user2); // Same ID
            assertThat(user1).isNotEqualTo(user3); // Different ID
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
            assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should pass validation with valid user")
        void shouldPassValidationWithValidUser() {
            // Given
            User user = User.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation with invalid email")
        void shouldFailValidationWithInvalidEmail() {
            // Given
            User user = User.builder()
                    .email("invalid-email")
                    .password("password123")
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("Email should be valid");
        }

        @Test
        @DisplayName("Should fail validation with blank email")
        void shouldFailValidationWithBlankEmail() {
            // Given
            User user = User.builder()
                    .email("")
                    .password("password123")
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("Email is required");
        }

        @Test
        @DisplayName("Should fail validation with short password")
        void shouldFailValidationWithShortPassword() {
            // Given
            User user = User.builder()
                    .email("test@example.com")
                    .password("123")
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("Password must be between 6 and 120 characters");
        }

        @Test
        @DisplayName("Should fail validation with blank password")
        void shouldFailValidationWithBlankPassword() {
            // Given
            User user = User.builder()
                    .email("test@example.com")
                    .password("")
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(2); // Both @NotBlank and @Size violations
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsAnyOf("Password is required", "Password must be between 6 and 120 characters");
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should add role correctly")
        void shouldAddRoleCorrectly() {
            // Given
            User user = User.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();

            // When
            user.addRole(Role.ROLE_USER);

            // Then
            assertThat(user.getRoles()).containsExactly(Role.ROLE_USER);
            assertThat(user.hasRole(Role.ROLE_USER)).isTrue();
            assertThat(user.hasRole(Role.ROLE_ADMIN)).isFalse();
        }

        @Test
        @DisplayName("Should remove role correctly")
        void shouldRemoveRoleCorrectly() {
            // Given
            User user = User.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();
            user.addRole(Role.ROLE_USER);
            user.addRole(Role.ROLE_ADMIN);

            // When
            user.removeRole(Role.ROLE_USER);

            // Then
            assertThat(user.getRoles()).containsExactly(Role.ROLE_ADMIN);
            assertThat(user.hasRole(Role.ROLE_USER)).isFalse();
            assertThat(user.hasRole(Role.ROLE_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("Should handle multiple roles")
        void shouldHandleMultipleRoles() {
            // Given
            User user = User.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();

            // When
            user.addRole(Role.ROLE_USER);
            user.addRole(Role.ROLE_ADMIN);

            // Then
            assertThat(user.getRoles()).containsExactlyInAnyOrder(Role.ROLE_USER, Role.ROLE_ADMIN);
            assertThat(user.hasRole(Role.ROLE_USER)).isTrue();
            assertThat(user.hasRole(Role.ROLE_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("Should create user with createUser helper")
        void shouldCreateUserWithCreateUserHelper() {
            // Given & When
            User user = User.createUser("test@example.com", "password123");

            // Then
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getPassword()).isEqualTo("password123");
            assertThat(user.getRoles()).containsExactly(Role.ROLE_USER);
            assertThat(user.getEnabled()).isTrue();
        }

        @Test
        @DisplayName("Should create admin with createAdmin helper")
        void shouldCreateAdminWithCreateAdminHelper() {
            // Given & When
            User admin = User.createAdmin("admin@example.com", "adminPassword");

            // Then
            assertThat(admin.getEmail()).isEqualTo("admin@example.com");
            assertThat(admin.getPassword()).isEqualTo("adminPassword");
            assertThat(admin.getRoles()).containsExactly(Role.ROLE_ADMIN);
            assertThat(admin.getEnabled()).isTrue();
        }
    }
}
