package com.firstaiAutomationSystem.project.exception;

public class UnsupportedDocumentTypeException extends AutomationException {
    public UnsupportedDocumentTypeException(String message) {
        super(message, "UNSUPPORTED_DOCUMENT_TYPE");
    }
}
