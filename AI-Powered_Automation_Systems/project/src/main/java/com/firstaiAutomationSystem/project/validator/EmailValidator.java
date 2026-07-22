package com.firstaiAutomationSystem.project.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class EmailValidator {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // DOCX
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".docx");

    public void validateDocument(MultipartFile document) {
        // Check file size
        if (document.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum limit of %d MB",
                            MAX_FILE_SIZE / (1024 * 1024))
            );
        }

        // Check content type
        String contentType = document.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Invalid file type. Only PDF and DOCX files are allowed"
            );
        }

        // Check file extension (double-check for spoofed content types)
        String filename = document.getOriginalFilename();
        if (filename == null || ALLOWED_EXTENSIONS.stream()
                .noneMatch(ext -> filename.toLowerCase().endsWith(ext))) {
            throw new IllegalArgumentException(
                    "Invalid file extension. Only .pdf and .docx files are allowed"
            );
        }

        // Check for null bytes (potential malicious files)
        if (filename.contains("\0")) {
            throw new IllegalArgumentException("Invalid filename");
        }
    }
}
