package org.wydcull.ai_chatbot_project.exception;

import lombok.Getter;

@Getter
public class ChatbotException extends RuntimeException {
    private final String errorCode;
    private final Object[] args;

    public ChatbotException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }

    public ChatbotException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public ChatbotException(String message, String errorCode, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
}