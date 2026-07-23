package com.firstaiAutomationSystem.project.exception;

public class AiParsingException extends AutomationException {
    public AiParsingException(String message, Throwable cause) {
        super(message, "AI_PARSING_ERROR", cause);
    }
}
