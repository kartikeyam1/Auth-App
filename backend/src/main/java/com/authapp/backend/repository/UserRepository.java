package com.authapp.backend.repository;

import com.authapp.backend.entity.Role;
import com.authapp.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address.
     * Used for authentication and user lookup.
     *
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email.
     *
     * @param email the email address to check
     * @return true if user exists, false otherwise
     */
    Boolean existsByEmail(String email);

    /**
     * Find all users with a specific role.
     *
     * @param role the role to search for
     * @return list of users with the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") Role role);

    /**
     * Find all enabled users.
     *
     * @return list of enabled users
     */
    List<User> findByEnabledTrue();

    /**
     * Find all disabled users.
     *
     * @return list of disabled users
     */
    List<User> findByEnabledFalse();

    /**
     * Count total number of users.
     *
     * @return total user count
     */
    @Query("SELECT COUNT(u) FROM User u")
    Long countTotalUsers();

    /**
     * Count users by role.
     *
     * @param role the role to count
     * @return number of users with the specified role
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role")
    Long countByRole(@Param("role") Role role);

    /**
     * Find users by email containing the search term (case insensitive).
     *
     * @param searchTerm the search term
     * @return list of users matching the search
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findByEmailContainingIgnoreCase(@Param("searchTerm") String searchTerm);
}
