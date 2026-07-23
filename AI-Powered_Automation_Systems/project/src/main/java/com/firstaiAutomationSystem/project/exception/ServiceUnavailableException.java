package com.firstaiAutomationSystem.project.exception;

public class ServiceUnavailableException extends AutomationException {
    public ServiceUnavailableException(String message) {
        super(message, "SERVICE_UNAVAILABLE");
    }
}
