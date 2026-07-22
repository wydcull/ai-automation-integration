package com.firstaiAutomationSystem.project.model;

import jakarta.validation.constraints.*;

public record EmailTriageRequest(
        @NotBlank(message = "Sender email is required")
        @Email(message = "Invalid email format",
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String senderEmail,

        @NotBlank(message = "Subject is required")
        @Size(min = 1, max = 500, message = "Subject must be between 1 and 500 characters")
        String subject,

        @NotBlank(message = "Body is required")
        @Size(min = 10, max = 10000, message = "Body must be between 10 and 10000 characters")
        String body
) {}

