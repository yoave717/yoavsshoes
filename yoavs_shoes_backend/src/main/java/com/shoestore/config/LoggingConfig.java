package com.shoestore.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;

/**
 * Logging configuration for different environments
 */
@Configuration
public class LoggingConfig {

    /**
     * Ensure logs directory exists
     */
    @PostConstruct
    public void init() {
        File logsDir = new File("./logs");
        if (!logsDir.exists()) {
            boolean created = logsDir.mkdirs();
            if (created) {
                System.out.println("Created logs directory: " + logsDir.getAbsolutePath());
            }
        }
    }

    /**
     * Development profile logging configuration
     */
    @Configuration
    @Profile("dev")
    static class DevelopmentLoggingConfig {

        @PostConstruct
        public void setupDevelopmentLogging() {
            System.setProperty("logging.level.com.shoestore", "DEBUG");
            System.setProperty("logging.level.org.springframework.security", "DEBUG");
            System.setProperty("logging.level.org.hibernate.SQL", "DEBUG");
            System.out.println("Development logging configuration applied");
        }
    }


}