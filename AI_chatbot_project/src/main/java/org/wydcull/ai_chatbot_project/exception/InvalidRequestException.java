package org.wydcull.ai_chatbot_project.exception;

public class InvalidRequestException extends ChatbotException {
    public InvalidRequestException(String message) {
        super(message, "INVALID_REQUEST");
    }

    public InvalidRequestException(String field, String reason) {
        super(String.format("Invalid %s: %s", field, reason),
                "INVALID_REQUEST", field, reason);
    }
}