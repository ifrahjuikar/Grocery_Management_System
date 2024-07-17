package com.springbootproject.springbootproject.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

// Utility class for logging errors using SLF4J.
public class LoggingErrorUtility {

    private static final Logger logger = LoggerFactory.getLogger(LoggingErrorUtility.class);
    private static final Properties logMessages = new Properties();

    // Static block to load error messages from the error.properties file.
    // If the file cannot be loaded, an error message is logged.
    static {
        try {
            logMessages.load(new ClassPathResource("error.properties").getInputStream());
        } catch (IOException e) {
            logger.error("Failed to load error.properties", e);
        }
    }

    // Logs an error message with parameters.
    public static void errorLogging(String key, Object... params) {
        String message = logMessages.getProperty(key, "Unknown error");
        logger.error(message, params);
    }
}
