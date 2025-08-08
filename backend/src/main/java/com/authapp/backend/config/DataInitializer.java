package com.authapp.backend.config;

import com.authapp.backend.entity.User;
import com.authapp.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initializer to create default users on application startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("🌱 Initializing default data...");
        
        // Create default admin user
        if (!userService.existsByEmail("admin@authapp.com")) {
            User admin = User.createAdmin("admin@authapp.com", passwordEncoder.encode("admin123"));
            userService.createUser(admin);
            log.info("✅ Default admin user created: admin@authapp.com / admin123");
        }
        
        // Create default regular user
        if (!userService.existsByEmail("user@authapp.com")) {
            User user = User.createUser("user@authapp.com", passwordEncoder.encode("user123"));
            userService.createUser(user);
            log.info("✅ Default user created: user@authapp.com / user123");
        }
        
        log.info("🎉 Data initialization completed!");
        log.info("📊 Total users in database: {}", userService.getTotalUserCount());
    }
}
