package com.authapp.backend.service;

import com.authapp.backend.entity.Role;
import com.authapp.backend.entity.User;
import com.authapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for User entity operations.
 * Handles business logic for user management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Create a new user.
     *
     * @param user the user to create
     * @return the saved user
     */
    public User createUser(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Find user by email.
     *
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by ID.
     *
     * @param id the user ID
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Get all users.
     *
     * @return list of all users
     */
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        log.debug("Retrieving all users");
        return userRepository.findAll();
    }

    /**
     * Find users by role.
     *
     * @param role the role to search for
     * @return list of users with the specified role
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        log.debug("Finding users by role: {}", role);
        return userRepository.findByRole(role);
    }

    /**
     * Update user.
     *
     * @param user the user to update
     * @return the updated user
     */
    public User updateUser(User user) {
        log.info("Updating user with ID: {}", user.getId());
        
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("User with ID " + user.getId() + " not found");
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Delete user by ID.
     *
     * @param id the user ID to delete
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User with ID " + id + " not found");
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    /**
     * Check if user exists by email.
     *
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get total user count.
     *
     * @return total number of users
     */
    @Transactional(readOnly = true)
    public Long getTotalUserCount() {
        return userRepository.countTotalUsers();
    }

    /**
     * Get user count by role.
     *
     * @param role the role to count
     * @return number of users with the specified role
     */
    @Transactional(readOnly = true)
    public Long getUserCountByRole(Role role) {
        return userRepository.countByRole(role);
    }

    /**
     * Search users by email containing term.
     *
     * @param searchTerm the search term
     * @return list of users matching the search
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByEmail(String searchTerm) {
        log.debug("Searching users by email containing: {}", searchTerm);
        return userRepository.findByEmailContainingIgnoreCase(searchTerm);
    }
}
