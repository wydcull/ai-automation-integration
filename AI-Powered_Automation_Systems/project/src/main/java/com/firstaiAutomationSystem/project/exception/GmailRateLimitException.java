package com.firstaiAutomationSystem.project.exception;

public class GmailRateLimitException extends AutomationException {
    private final long retryAfterSeconds;

    public GmailRateLimitException(String message, long retryAfterSeconds) {
        super(message, "GMAIL_RATE_LIMIT");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
