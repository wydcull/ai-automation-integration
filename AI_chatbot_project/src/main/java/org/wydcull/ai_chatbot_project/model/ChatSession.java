package org.wydcull.ai_chatbot_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime lastActivityAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer messageCount = 0;

    @Column(columnDefinition = "TEXT")
    private String conversationSummary;  // AI-generated summary of conversation

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime expiresAt;  // TTL

    private String userId;  // Optional: link to user

    private String metadata;  // JSON field for additional data

    public void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
        this.messageCount++;
    }
}