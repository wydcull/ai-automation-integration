package com.firstaiAutomationSystem.project.exception;

public class GmailQuotaExceededException extends AutomationException {
    public GmailQuotaExceededException(String message) {
        super(message, "GMAIL_QUOTA_EXCEEDED");
    }
}
