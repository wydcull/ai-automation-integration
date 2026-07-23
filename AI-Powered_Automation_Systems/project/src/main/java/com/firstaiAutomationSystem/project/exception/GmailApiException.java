package com.firstaiAutomationSystem.project.exception;

/**
 * Gmail API specific exceptions
 */
public class GmailApiException extends AutomationException {
    public GmailApiException(String message, Throwable cause) {
        super(message, "GMAIL_API_ERROR", cause);
    }
}

