package org.wydcull.ai_chatbot_project.exception;

public class AIServiceException extends ChatbotException {
    public AIServiceException(String message, Throwable cause) {
        super(message, "AI_SERVICE_ERROR", cause);
    }

    public AIServiceException(String message) {
        super(message, "AI_SERVICE_ERROR");
    }
}
