package com.firstaiAutomationSystem.project.exception;

public class AiServiceException extends AutomationException {
    public AiServiceException(String message, Throwable cause) {
        super(message, "AI_SERVICE_ERROR", cause);
    }
}