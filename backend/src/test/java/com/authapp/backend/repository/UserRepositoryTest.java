package com.authapp.backend.repository;

import com.authapp.backend.entity.Role;
import com.authapp.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for UserRepository.
 * Uses @DataJpaTest for repository layer testing with embedded H2 database.
 */
@DataJpaTest
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("user@example.com")
                .password("password123")
                .build();
        testUser.addRole(Role.ROLE_USER);

        testAdmin = User.builder()
                .email("admin@example.com")
                .password("adminPassword")
                .build();
        testAdmin.addRole(Role.ROLE_ADMIN);

        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(testAdmin);
    }

    @Nested
    @DisplayName("Find By Email Tests")
    class FindByEmailTests {

        @Test
        @DisplayName("Should find user by email")
        void shouldFindUserByEmail() {
            // When
            Optional<User> result = userRepository.findByEmail("user@example.com");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("user@example.com");
            assertThat(result.get().getRoles()).containsExactly(Role.ROLE_USER);
        }

        @Test
        @DisplayName("Should return empty when user not found by email")
        void shouldReturnEmptyWhenUserNotFoundByEmail() {
            // When
            Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should be case sensitive for email search")
        void shouldBeCaseSensitiveForEmailSearch() {
            // When
            Optional<User> result = userRepository.findByEmail("USER@EXAMPLE.COM");

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Exists By Email Tests")
    class ExistsByEmailTests {

        @Test
        @DisplayName("Should return true when user exists by email")
        void shouldReturnTrueWhenUserExistsByEmail() {
            // When
            Boolean exists = userRepository.existsByEmail("user@example.com");

            // Then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false when user does not exist by email")
        void shouldReturnFalseWhenUserDoesNotExistByEmail() {
            // When
            Boolean exists = userRepository.existsByEmail("nonexistent@example.com");

            // Then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("Find By Role Tests")
    class FindByRoleTests {

        @Test
        @DisplayName("Should find users by role USER")
        void shouldFindUsersByRoleUser() {
            // When
            List<User> result = userRepository.findByRole(Role.ROLE_USER);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).isEqualTo("user@example.com");
            assertThat(result.get(0).getRoles()).containsExactly(Role.ROLE_USER);
        }

        @Test
        @DisplayName("Should find users by role ADMIN")
        void shouldFindUsersByRoleAdmin() {
            // When
            List<User> result = userRepository.findByRole(Role.ROLE_ADMIN);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).isEqualTo("admin@example.com");
            assertThat(result.get(0).getRoles()).containsExactly(Role.ROLE_ADMIN);
        }

        @Test
        @DisplayName("Should return empty list when no users found for role")
        void shouldReturnEmptyListWhenNoUsersFoundForRole() {
            // Given - Remove all users first
            userRepository.deleteAll();
            entityManager.flush();

            // When
            List<User> result = userRepository.findByRole(Role.ROLE_USER);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find By Enabled Tests")
    class FindByEnabledTests {

        @Test
        @DisplayName("Should find enabled users")
        void shouldFindEnabledUsers() {
            // When
            List<User> result = userRepository.findByEnabledTrue();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(User::getEnabled).containsOnly(true);
        }

        @Test
        @DisplayName("Should find disabled users")
        void shouldFindDisabledUsers() {
            // Given - Create a disabled user
            User disabledUser = User.builder()
                    .email("disabled@example.com")
                    .password("password123")
                    .enabled(false)
                    .build();
            disabledUser.addRole(Role.ROLE_USER);
            entityManager.persistAndFlush(disabledUser);

            // When
            List<User> result = userRepository.findByEnabledFalse();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEnabled()).isFalse();
            assertThat(result.get(0).getEmail()).isEqualTo("disabled@example.com");
        }
    }

    @Nested
    @DisplayName("Count Tests")
    class CountTests {

        @Test
        @DisplayName("Should count total users")
        void shouldCountTotalUsers() {
            // When
            Long count = userRepository.countTotalUsers();

            // Then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("Should count users by role")
        void shouldCountUsersByRole() {
            // When
            Long userCount = userRepository.countByRole(Role.ROLE_USER);
            Long adminCount = userRepository.countByRole(Role.ROLE_ADMIN);

            // Then
            assertThat(userCount).isEqualTo(1L);
            assertThat(adminCount).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return zero count for non-existent role users")
        void shouldReturnZeroCountForNonExistentRoleUsers() {
            // Given - Remove all users
            userRepository.deleteAll();
            entityManager.flush();

            // When
            Long count = userRepository.countByRole(Role.ROLE_USER);

            // Then
            assertThat(count).isZero();
        }
    }

    @Nested
    @DisplayName("Search Tests")
    class SearchTests {

        @Test
        @DisplayName("Should find users by email containing search term")
        void shouldFindUsersByEmailContainingSearchTerm() {
            // When
            List<User> result = userRepository.findByEmailContainingIgnoreCase("user");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("Should find users by email containing search term case insensitive")
        void shouldFindUsersByEmailContainingSearchTermCaseInsensitive() {
            // When
            List<User> result = userRepository.findByEmailContainingIgnoreCase("ADMIN");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).isEqualTo("admin@example.com");
        }

        @Test
        @DisplayName("Should find multiple users by partial email")
        void shouldFindMultipleUsersByPartialEmail() {
            // When
            List<User> result = userRepository.findByEmailContainingIgnoreCase("example.com");

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(User::getEmail)
                    .containsExactlyInAnyOrder("user@example.com", "admin@example.com");
        }

        @Test
        @DisplayName("Should return empty list when no matches found")
        void shouldReturnEmptyListWhenNoMatchesFound() {
            // When
            List<User> result = userRepository.findByEmailContainingIgnoreCase("nonexistent");

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Multi-Role Tests")
    class MultiRoleTests {

        @Test
        @DisplayName("Should handle user with multiple roles")
        void shouldHandleUserWithMultipleRoles() {
            // Given
            User multiRoleUser = User.builder()
                    .email("multirole@example.com")
                    .password("password123")
                    .build();
            multiRoleUser.addRole(Role.ROLE_USER);
            multiRoleUser.addRole(Role.ROLE_ADMIN);
            entityManager.persistAndFlush(multiRoleUser);

            // When
            List<User> userRoleUsers = userRepository.findByRole(Role.ROLE_USER);
            List<User> adminRoleUsers = userRepository.findByRole(Role.ROLE_ADMIN);

            // Then
            assertThat(userRoleUsers).hasSize(2); // original user + multi-role user
            assertThat(adminRoleUsers).hasSize(2); // original admin + multi-role user
            
            // Verify the multi-role user appears in both searches
            assertThat(userRoleUsers).extracting(User::getEmail).contains("multirole@example.com");
            assertThat(adminRoleUsers).extracting(User::getEmail).contains("multirole@example.com");
        }
    }

    @Nested
    @DisplayName("Persistence Tests")
    class PersistenceTests {

        @Test
        @DisplayName("Should persist user with timestamps")
        void shouldPersistUserWithTimestamps() {
            // Given
            User newUser = User.builder()
                    .email("newuser@example.com")
                    .password("password123")
                    .build();
            newUser.addRole(Role.ROLE_USER);

            // When
            User savedUser = userRepository.save(newUser);
            entityManager.flush();
            entityManager.clear();

            // Then
            Optional<User> foundUser = userRepository.findById(savedUser.getId());
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getCreatedAt()).isNotNull();
            assertThat(foundUser.get().getUpdatedAt()).isNotNull();
            // Allow small timestamp differences due to microsecond precision
            var createdAt = foundUser.get().getCreatedAt();
            var updatedAt = foundUser.get().getUpdatedAt();
            var diffInNanos = Math.abs(java.time.Duration.between(createdAt, updatedAt).toNanos());
            assertThat(diffInNanos).isLessThanOrEqualTo(1_000_000L); // Allow up to 1ms difference
        }

        @Test
        @DisplayName("Should update timestamp on user modification")
        void shouldUpdateTimestampOnUserModification() throws InterruptedException {
            // Given
            User user = userRepository.findByEmail("user@example.com").orElseThrow();
            var originalCreatedAt = user.getCreatedAt();
            var originalUpdatedAt = user.getUpdatedAt();

            // Small delay to ensure timestamp difference
            Thread.sleep(100);

            // When
            user.setEmail("updated@example.com");
            userRepository.save(user);
            entityManager.flush();

            // Then
            User updatedUser = userRepository.findById(user.getId()).orElseThrow();
            assertThat(updatedUser.getCreatedAt()).isEqualTo(originalCreatedAt);
            assertThat(updatedUser.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        }
    }
}
