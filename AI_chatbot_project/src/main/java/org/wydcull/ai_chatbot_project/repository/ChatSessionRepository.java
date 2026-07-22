package org.wydcull.ai_chatbot_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wydcull.ai_chatbot_project.model.ChatSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findBySessionId(String sessionId);

    // Find expired sessions for cleanup
    List<ChatSession> findByExpiresAtBeforeAndActiveTrue(LocalDateTime now);

    // Find inactive sessions (no activity for X days)
    @Query("SELECT cs FROM ChatSession cs WHERE cs.lastActivityAt < :cutoffDate AND cs.active = true")
    List<ChatSession> findInactiveSessions(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Deactivate expired sessions
    @Modifying
    @Query("UPDATE ChatSession cs SET cs.active = false WHERE cs.expiresAt < :now")
    int deactivateExpiredSessions(@Param("now") LocalDateTime now);

    // Count active sessions
    long countByActiveTrue();
}