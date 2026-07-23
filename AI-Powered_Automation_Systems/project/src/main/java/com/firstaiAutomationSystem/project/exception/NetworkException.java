package com.firstaiAutomationSystem.project.exception;
/**
 * Network and transient errors
 */
public class NetworkException extends AutomationException {
    public NetworkException(String message, Throwable cause) {
        super(message, "NETWORK_ERROR", cause);
    }
}

