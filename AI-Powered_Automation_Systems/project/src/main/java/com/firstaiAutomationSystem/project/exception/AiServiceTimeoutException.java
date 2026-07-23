package com.firstaiAutomationSystem.project.exception;

public class AiServiceTimeoutException extends AutomationException {
    public AiServiceTimeoutException(String message) {
        super(message, "AI_TIMEOUT");
    }
}
