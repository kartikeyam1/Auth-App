package com.authapp.backend.service;

import com.authapp.backend.entity.Role;
import com.authapp.backend.entity.User;
import com.authapp.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Uses Mockito to mock dependencies and test business logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("password123")
                .build();
        testUser.addRole(Role.ROLE_USER);

        testAdmin = User.builder()
                .id(2L)
                .email("admin@example.com")
                .password("adminPassword")
                .build();
        testAdmin.addRole(Role.ROLE_ADMIN);
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() {
            // Given
            when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            User result = userService.createUser(testUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("user@example.com");
            verify(userRepository).existsByEmail("user@example.com");
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when user already exists")
        void shouldThrowExceptionWhenUserAlreadyExists() {
            // Given
            when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.createUser(testUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User with email user@example.com already exists");

            verify(userRepository).existsByEmail("user@example.com");
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Find User Tests")
    class FindUserTests {

        @Test
        @DisplayName("Should find user by email")
        void shouldFindUserByEmail() {
            // Given
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.findByEmail("user@example.com");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("user@example.com");
            verify(userRepository).findByEmail("user@example.com");
        }

        @Test
        @DisplayName("Should return empty when user not found by email")
        void shouldReturnEmptyWhenUserNotFoundByEmail() {
            // Given
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            // When
            Optional<User> result = userService.findByEmail("nonexistent@example.com");

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findByEmail("nonexistent@example.com");
        }

        @Test
        @DisplayName("Should find user by ID")
        void shouldFindUserById() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when user not found by ID")
        void shouldReturnEmptyWhenUserNotFoundById() {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userService.findById(999L);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findById(999L);
        }

        @Test
        @DisplayName("Should find all users")
        void shouldFindAllUsers() {
            // Given
            List<User> users = List.of(testUser, testAdmin);
            when(userRepository.findAll()).thenReturn(users);

            // When
            List<User> result = userService.findAllUsers();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(testUser, testAdmin);
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should find users by role")
        void shouldFindUsersByRole() {
            // Given
            List<User> adminUsers = List.of(testAdmin);
            when(userRepository.findByRole(Role.ROLE_ADMIN)).thenReturn(adminUsers);

            // When
            List<User> result = userService.findByRole(Role.ROLE_ADMIN);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(testAdmin);
            verify(userRepository).findByRole(Role.ROLE_ADMIN);
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Given
            when(userRepository.existsById(testUser.getId())).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            User result = userService.updateUser(testUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testUser.getId());
            verify(userRepository).existsById(testUser.getId());
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            // Given
            when(userRepository.existsById(testUser.getId())).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(testUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User with ID 1 not found");

            verify(userRepository).existsById(testUser.getId());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Given
            when(userRepository.existsById(1L)).thenReturn(true);

            // When
            userService.deleteUser(1L);

            // Then
            verify(userRepository).existsById(1L);
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            // Given
            when(userRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User with ID 999 not found");

            verify(userRepository).existsById(999L);
            verify(userRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Utility Methods Tests")
    class UtilityMethodsTests {

        @Test
        @DisplayName("Should check if user exists by email")
        void shouldCheckIfUserExistsByEmail() {
            // Given
            when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

            // When
            boolean exists = userService.existsByEmail("user@example.com");

            // Then
            assertThat(exists).isTrue();
            verify(userRepository).existsByEmail("user@example.com");
        }

        @Test
        @DisplayName("Should get total user count")
        void shouldGetTotalUserCount() {
            // Given
            when(userRepository.countTotalUsers()).thenReturn(5L);

            // When
            Long count = userService.getTotalUserCount();

            // Then
            assertThat(count).isEqualTo(5L);
            verify(userRepository).countTotalUsers();
        }

        @Test
        @DisplayName("Should get user count by role")
        void shouldGetUserCountByRole() {
            // Given
            when(userRepository.countByRole(Role.ROLE_ADMIN)).thenReturn(2L);

            // When
            Long count = userService.getUserCountByRole(Role.ROLE_ADMIN);

            // Then
            assertThat(count).isEqualTo(2L);
            verify(userRepository).countByRole(Role.ROLE_ADMIN);
        }

        @Test
        @DisplayName("Should search users by email")
        void shouldSearchUsersByEmail() {
            // Given
            List<User> users = List.of(testUser);
            when(userRepository.findByEmailContainingIgnoreCase("user")).thenReturn(users);

            // When
            List<User> result = userService.searchUsersByEmail("user");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(testUser);
            verify(userRepository).findByEmailContainingIgnoreCase("user");
        }
    }
}
