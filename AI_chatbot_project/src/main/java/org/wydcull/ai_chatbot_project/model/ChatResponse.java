package org.wydcull.ai_chatbot_project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private String sessionId;
    private String reply;
    private LocalDateTime timestamp;
}