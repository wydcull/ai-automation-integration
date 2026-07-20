package com.firstaiAutomationSystem.project.model;

import java.util.Map;

public record DocumentExtractionResult(
        String documentType,           // INVOICE, RESUME, LETTER, CONTRACT, REPORT, etc.
        String summary,                // Brief summary of the document
        Map<String, Object> extractedFields  // Flexible key-value pairs
) {
}