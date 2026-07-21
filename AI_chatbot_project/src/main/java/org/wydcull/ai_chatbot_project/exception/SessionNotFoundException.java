package org.wydcull.ai_chatbot_project.exception;

public class SessionNotFoundException extends ChatbotException {
    public SessionNotFoundException(String sessionId) {
        super("Session not found: " + sessionId, "SESSION_NOT_FOUND", sessionId);
    }
}
