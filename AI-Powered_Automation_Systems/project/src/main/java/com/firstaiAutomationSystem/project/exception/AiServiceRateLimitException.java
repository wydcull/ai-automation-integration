package com.firstaiAutomationSystem.project.exception;

public class AiServiceRateLimitException extends AutomationException {
    private final long retryAfterSeconds;

    public AiServiceRateLimitException(String message, long retryAfterSeconds) {
        super(message, "AI_RATE_LIMIT");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}