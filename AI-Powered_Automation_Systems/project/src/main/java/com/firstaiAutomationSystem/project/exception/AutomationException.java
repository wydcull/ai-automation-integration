package com.firstaiAutomationSystem.project.exception;

/**
 * Base exception for all automation system errors
 */
public class AutomationException extends RuntimeException {
    private final String errorCode;

    public AutomationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AutomationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}