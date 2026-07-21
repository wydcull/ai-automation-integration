package org.wydcull.ai_chatbot_project.exception;

public class RateLimitExceededException extends ChatbotException {
    public RateLimitExceededException(String message) {
        super(message, "RATE_LIMIT_EXCEEDED");
    }
}