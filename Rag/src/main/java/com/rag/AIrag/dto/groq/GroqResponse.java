package com.rag.AIrag.dto.groq;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GroqResponse {
    private List<Choice> choices;
    private Usage usage;

    @Data
    @NoArgsConstructor
    public static class Choice {
        private Message message;
        private Integer index;
        private String finish_reason;
    }

    @Data
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
        private String reasoning;  // Qwen thinking goes here when format=parsed
    }

    @Data
    @NoArgsConstructor
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }

    public String getGeneratedText() {
        if (choices == null || choices.isEmpty() || choices.get(0).getMessage() == null) {
            return "";
        }

        Message message = choices.get(0).getMessage();
        String content = stripThinking(message.getContent());

        if (content != null && !content.isBlank()) {
            return content;
        }

        // Fallback if API put the visible answer only in reasoning
        return stripThinking(message.getReasoning());
    }

    private String stripThinking(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        // Remove complete think blocks only (do not wipe unclosed text)
        String cleaned = content.replaceAll("(?s)<think>.*?</think>", "");
        cleaned = cleaned.replaceAll("(?s)</think>", "");
        return cleaned.trim();
    }
}