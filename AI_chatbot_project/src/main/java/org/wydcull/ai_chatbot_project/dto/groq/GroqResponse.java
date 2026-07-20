package org.wydcull.ai_chatbot_project.dto.groq;

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
    }

    @Data
    @NoArgsConstructor
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }

    public String getGeneratedText() {
        if (choices != null && !choices.isEmpty()) {
            Choice choice = choices.get(0);
            if (choice.getMessage() != null && choice.getMessage().getContent() != null) {
                return choice.getMessage().getContent();
            }
        }
        return "";
    }
}