package com.firstaiAutomationSystem.project.util;

import org.owasp.encoder.Encode;
import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

    /**
     * Sanitize input to prevent XSS attacks
     */
    public String sanitizeHtml(String input) {
        if (input == null) return null;

        // Remove HTML tags and encode special characters
        return Encode.forHtml(input.trim());
    }

    /**
     * Sanitize for SQL-like patterns (though JPA handles this)
     */
    public String sanitizeForDatabase(String input) {
        if (input == null) return null;

        // Remove control characters and normalize whitespace
        return input.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Sanitize email addresses
     */
    public String sanitizeEmail(String email) {
        if (email == null) return null;

        return email.toLowerCase()
                .replaceAll("[^a-z0-9@._+-]", "")
                .trim();
    }
}
