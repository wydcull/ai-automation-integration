package com.firstaiAutomationSystem.project.exception;

public class GmailAuthenticationException extends AutomationException {
    public GmailAuthenticationException(String message, Throwable cause) {
        super(message, "GMAIL_AUTH_ERROR", cause);
    }
}
