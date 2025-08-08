package com.authapp.backend.entity;

import lombok.Getter;

/**
 * Enumeration representing user roles in the application.
 * Used for role-based access control (RBAC).
 */
@Getter
public enum Role {
    ROLE_USER("User"),
    ROLE_ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
