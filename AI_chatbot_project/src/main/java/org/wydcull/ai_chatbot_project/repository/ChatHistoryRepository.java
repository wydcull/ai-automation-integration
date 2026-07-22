package org.wydcull.ai_chatbot_project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wydcull.ai_chatbot_project.model.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatMessage, Long> {

    // Existing methods
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    List<ChatMessage> findTop5BySessionIdOrderByCreatedAtDesc(String sessionId);

    // NEW: Paginated history
    Page<ChatMessage> findBySessionIdOrderByCreatedAtDesc(String sessionId, Pageable pageable);

    // REPLACE with this (using Pageable):
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sessionId = :sessionId " +
            "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findRecentMessages(
            @Param("sessionId") String sessionId,
            Pageable pageable
    );

    // NEW: Count messages in a session
    long countBySessionId(String sessionId);

    // NEW: Delete old messages (for cleanup)
    @Modifying
    @Query("DELETE FROM ChatMessage cm WHERE cm.createdAt < :cutoffDate")
    int deleteOldMessages(@Param("cutoffDate") LocalDateTime cutoffDate);

    // NEW: Find messages between dates
    List<ChatMessage> findBySessionIdAndCreatedAtBetweenOrderByCreatedAtAsc(
            String sessionId,
            LocalDateTime start,
            LocalDateTime end
    );

    // NEW: Get messages after a specific ID (for real-time updates)
    List<ChatMessage> findBySessionIdAndIdGreaterThanOrderByCreatedAtAsc(
            String sessionId,
            Long afterId
    );
}