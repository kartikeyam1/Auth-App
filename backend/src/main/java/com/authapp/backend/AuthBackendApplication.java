package com.authapp.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class AuthBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthBackendApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("🚀 Auth Backend Application Started Successfully!");
        log.info("📊 H2 Console: http://localhost:8080/api/h2-console");
        log.info("🌐 API Base URL: http://localhost:8080/api");
        log.info("📝 JDBC URL: jdbc:h2:file:./data/authapp");
        log.info("👤 H2 Username: sa");
        log.info("🔒 H2 Password: password");
    }
}
