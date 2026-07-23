package com.firstaiAutomationSystem.project.exception;

/**
 * Document processing exceptions
 */
public class DocumentProcessingException extends AutomationException {
    public DocumentProcessingException(String message, Throwable cause) {
        super(message, "DOCUMENT_PROCESSING_ERROR", cause);
    }
}

