package com.authapp.backend.controller;

import com.authapp.backend.dto.CreateUserDto;
import com.authapp.backend.entity.Role;
import com.authapp.backend.entity.User;
import com.authapp.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TestController.
 * Uses MockMvc for web layer testing and mocks service dependencies.
 */
@WebMvcTest(TestController.class)
@DisplayName("TestController Tests")
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testUser.addRole(Role.ROLE_USER);

        createUserDto = CreateUserDto.builder()
                .email("test@example.com")
                .password("password123")
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }

    @Nested
    @DisplayName("Health Check Tests")
    class HealthCheckTests {

        @Test
        @DisplayName("Should return health status")
        void shouldReturnHealthStatus() throws Exception {
            // Given
            when(userService.getTotalUserCount()).thenReturn(5L);

            // When & Then
            mockMvc.perform(get("/test/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.message").value("Auth Backend is running"))
                    .andExpect(jsonPath("$.database").value("H2 Connected"))
                    .andExpect(jsonPath("$.totalUsers").value(5))
                    .andExpect(jsonPath("$.timestamp").exists());

            verify(userService).getTotalUserCount();
        }
    }

    @Nested
    @DisplayName("User CRUD Tests")
    class UserCrudTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() throws Exception {
            // Given
            when(userService.createUser(any(User.class))).thenReturn(testUser);

            // When & Then
            mockMvc.perform(post("/test/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createUserDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                    .andExpect(jsonPath("$.enabled").value(true));

            verify(userService).createUser(any(User.class));
        }

        @Test
        @DisplayName("Should fail to create user with invalid email")
        void shouldFailToCreateUserWithInvalidEmail() throws Exception {
            // Given
            CreateUserDto invalidDto = CreateUserDto.builder()
                    .email("invalid-email")
                    .password("password123")
                    .build();

            // When & Then
            mockMvc.perform(post("/test/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).createUser(any(User.class));
        }

        @Test
        @DisplayName("Should fail to create user with short password")
        void shouldFailToCreateUserWithShortPassword() throws Exception {
            // Given
            CreateUserDto invalidDto = CreateUserDto.builder()
                    .email("test@example.com")
                    .password("123")
                    .build();

            // When & Then
            mockMvc.perform(post("/test/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).createUser(any(User.class));
        }

        @Test
        @DisplayName("Should get all users")
        void shouldGetAllUsers() throws Exception {
            // Given
            List<User> users = List.of(testUser);
            when(userService.findAllUsers()).thenReturn(users);

            // When & Then
            mockMvc.perform(get("/test/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].email").value("test@example.com"));

            verify(userService).findAllUsers();
        }

        @Test
        @DisplayName("Should get user by ID")
        void shouldGetUserById() throws Exception {
            // Given
            when(userService.findById(1L)).thenReturn(Optional.of(testUser));

            // When & Then
            mockMvc.perform(get("/test/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("test@example.com"));

            verify(userService).findById(1L);
        }

        @Test
        @DisplayName("Should return 404 when user not found by ID")
        void shouldReturn404WhenUserNotFoundById() throws Exception {
            // Given
            when(userService.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/test/users/999"))
                    .andExpect(status().isNotFound());

            verify(userService).findById(999L);
        }

        @Test
        @DisplayName("Should get user by email")
        void shouldGetUserByEmail() throws Exception {
            // Given
            when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            // When & Then
            mockMvc.perform(get("/test/users/email/test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.email").value("test@example.com"));

            verify(userService).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("Should get users by role")
        void shouldGetUsersByRole() throws Exception {
            // Given
            List<User> users = List.of(testUser);
            when(userService.findByRole(Role.ROLE_USER)).thenReturn(users);

            // When & Then
            mockMvc.perform(get("/test/users/role/ROLE_USER"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].roles[0]").value("ROLE_USER"));

            verify(userService).findByRole(Role.ROLE_USER);
        }

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() throws Exception {
            // Given
            doNothing().when(userService).deleteUser(1L);

            // When & Then
            mockMvc.perform(delete("/test/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("User deleted successfully"));

            verify(userService).deleteUser(1L);
        }

        @Test
        @DisplayName("Should handle delete user error")
        void shouldHandleDeleteUserError() throws Exception {
            // Given
            doThrow(new RuntimeException("User not found")).when(userService).deleteUser(999L);

            // When & Then
            mockMvc.perform(delete("/test/users/999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error").value("User not found"));

            verify(userService).deleteUser(999L);
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {

        @Test
        @DisplayName("Should get database statistics")
        void shouldGetDatabaseStatistics() throws Exception {
            // Given
            when(userService.getTotalUserCount()).thenReturn(10L);
            when(userService.getUserCountByRole(Role.ROLE_ADMIN)).thenReturn(2L);
            when(userService.getUserCountByRole(Role.ROLE_USER)).thenReturn(8L);

            // When & Then
            mockMvc.perform(get("/test/stats"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.totalUsers").value(10))
                    .andExpect(jsonPath("$.adminUsers").value(2))
                    .andExpect(jsonPath("$.regularUsers").value(8));

            verify(userService).getTotalUserCount();
            verify(userService).getUserCountByRole(Role.ROLE_ADMIN);
            verify(userService).getUserCountByRole(Role.ROLE_USER);
        }
    }

    @Nested
    @DisplayName("Data Initialization Tests")
    class DataInitializationTests {

        @Test
        @DisplayName("Should initialize sample data successfully")
        void shouldInitializeSampleDataSuccessfully() throws Exception {
            // Given
            when(userService.existsByEmail("admin@authapp.com")).thenReturn(false);
            when(userService.existsByEmail("user@authapp.com")).thenReturn(false);
            when(userService.createUser(any(User.class))).thenReturn(testUser);

            // When & Then
            mockMvc.perform(post("/test/init-data"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Sample data initialized successfully"));

            verify(userService, times(2)).existsByEmail(anyString());
            verify(userService, times(2)).createUser(any(User.class));
        }

        @Test
        @DisplayName("Should handle existing users during initialization")
        void shouldHandleExistingUsersDuringInitialization() throws Exception {
            // Given
            when(userService.existsByEmail("admin@authapp.com")).thenReturn(true);
            when(userService.existsByEmail("user@authapp.com")).thenReturn(true);

            // When & Then
            mockMvc.perform(post("/test/init-data"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Sample data initialized successfully"));

            verify(userService, times(2)).existsByEmail(anyString());
            verify(userService, never()).createUser(any(User.class));
        }

        @Test
        @DisplayName("Should handle initialization error")
        void shouldHandleInitializationError() throws Exception {
            // Given
            when(userService.existsByEmail(anyString())).thenReturn(false);
            when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(post("/test/init-data"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error").value("Database error"));

            verify(userService).createUser(any(User.class));
        }
    }
}
