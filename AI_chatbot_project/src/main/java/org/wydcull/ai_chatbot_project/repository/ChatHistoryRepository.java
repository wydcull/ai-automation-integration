package org.wydcull.ai_chatbot_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wydcull.ai_chatbot_project.model.ChatMessage;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatMessage, Long> {

    // Get last N messages for a session, ordered by creation time
    List<ChatMessage> findTop10BySessionIdOrderByCreatedAtAsc(String sessionId);

    // Get all messages for a session
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    // Count messages in a session
    long countBySessionId(String sessionId);

    List<ChatMessage> findTop5BySessionIdOrderByCreatedAtAsc(String sessionId);
}