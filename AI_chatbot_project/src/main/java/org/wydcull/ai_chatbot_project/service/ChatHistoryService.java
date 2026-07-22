package org.wydcull.ai_chatbot_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wydcull.ai_chatbot_project.dto.PaginatedResponse;
import org.wydcull.ai_chatbot_project.exception.SessionNotFoundException;
import org.wydcull.ai_chatbot_project.model.ChatMessage;
import org.wydcull.ai_chatbot_project.model.ChatSession;
import org.wydcull.ai_chatbot_project.repository.ChatHistoryRepository;
import org.wydcull.ai_chatbot_project.repository.ChatSessionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final GroqService groqService;

    // Configuration constants
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int SESSION_TTL_DAYS = 30;
    private static final int CLEANUP_RETENTION_DAYS = 90;
    private static final int SUMMARIZATION_THRESHOLD = 20;  // Summarize after 20 messages

    /**
     * Get paginated chat history
     */
    public PaginatedResponse<ChatMessage> getHistory(String sessionId, int page, int size) {
        log.info("Fetching paginated history for session: {}, page: {}, size: {}",
                sessionId, page, size);

        // Validate page size
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        if (size <= 0) {
            size = DEFAULT_PAGE_SIZE;
        }

        // Verify session exists
        ChatSession session = getOrCreateSession(sessionId);

        // Create pageable (descending order - newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Fetch paginated data
        Page<ChatMessage> messagePage = chatHistoryRepository
                .findBySessionIdOrderByCreatedAtDesc(sessionId, pageable);

        // FIX: Create a new mutable ArrayList before reversing
        List<ChatMessage> content = new ArrayList<>(messagePage.getContent());
        Collections.reverse(content);

        return PaginatedResponse.of(
                content,
                page,
                size,
                messagePage.getTotalElements()
        );
    }

    /**
     * Get recent messages for AI context (non-paginated)
     */
    /**
     * Get recent messages for AI context (non-paginated)
     */
    /**
     * Get recent messages for AI context (non-paginated)
     */
    public List<ChatMessage> getRecentMessagesForContext(String sessionId, int limit) {
        log.debug("Fetching {} recent messages for context", limit);

        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        List<ChatMessage> messages = chatHistoryRepository.findRecentMessages(sessionId, pageable);

        // FIX: Create new mutable list before reversing
        List<ChatMessage> mutableMessages = new ArrayList<>(messages);
        Collections.reverse(mutableMessages);

        return mutableMessages;
    }
    /**
     * Save message and update session metadata
     */
    @Transactional
    public ChatMessage saveMessage(String sessionId, ChatMessage.Role role, String content) {
        // Get or create session
        ChatSession session = getOrCreateSession(sessionId);

        // Save message
        ChatMessage message = new ChatMessage(sessionId, role, content);
        message = chatHistoryRepository.save(message);

        // Update session activity
        session.updateActivity();
        chatSessionRepository.save(session);

        // Check if summarization is needed
        if (session.getMessageCount() % SUMMARIZATION_THRESHOLD == 0) {
            scheduleConversationSummarization(sessionId);
        }

        return message;
    }

    /**
     * Get or create chat session
     */
    private ChatSession getOrCreateSession(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId)
                .orElseGet(() -> createNewSession(sessionId));
    }

    /**
     * Create new chat session
     */
    @Transactional
    private ChatSession createNewSession(String sessionId) {
        log.info("Creating new chat session: {}", sessionId);

        ChatSession session = new ChatSession();
        session.setSessionId(sessionId);
        session.setCreatedAt(LocalDateTime.now());
        session.setLastActivityAt(LocalDateTime.now());
        session.setMessageCount(0);
        session.setActive(true);
        session.setExpiresAt(LocalDateTime.now().plusDays(SESSION_TTL_DAYS));

        return chatSessionRepository.save(session);
    }

    /**
     * Get session metadata
     */
    public ChatSession getSessionMetadata(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
    }

    /**
     * Clear chat history for a session
     */
    @Transactional
    public void clearHistory(String sessionId) {
        log.info("Clearing history for session: {}", sessionId);

        List<ChatMessage> messages = chatHistoryRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId);

        chatHistoryRepository.deleteAll(messages);

        // Reset session metadata
        chatSessionRepository.findBySessionId(sessionId)
                .ifPresent(session -> {
                    session.setMessageCount(0);
                    session.setConversationSummary(null);
                    chatSessionRepository.save(session);
                });
    }

    /**
     * Delete session completely
     */
    @Transactional
    public void deleteSession(String sessionId) {
        log.info("Deleting session: {}", sessionId);

        // Delete all messages
        clearHistory(sessionId);

        // Delete session
        chatSessionRepository.findBySessionId(sessionId)
                .ifPresent(chatSessionRepository::delete);
    }

    /**
     * Extend session TTL
     */
    @Transactional
    public void extendSessionTTL(String sessionId, int additionalDays) {
        chatSessionRepository.findBySessionId(sessionId)
                .ifPresent(session -> {
                    session.setExpiresAt(session.getExpiresAt().plusDays(additionalDays));
                    chatSessionRepository.save(session);
                    log.info("Extended TTL for session {} by {} days", sessionId, additionalDays);
                });
    }

    /**
     * Generate conversation summary using AI
     */
    @Transactional
    public void generateConversationSummary(String sessionId) {
        log.info("Generating conversation summary for session: {}", sessionId);

        ChatSession session = chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        // Get all messages
        List<ChatMessage> messages = chatHistoryRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId);

        if (messages.isEmpty()) {
            return;
        }

        // Build conversation text
        StringBuilder conversation = new StringBuilder();
        for (ChatMessage msg : messages) {
            conversation.append(msg.getRole())
                    .append(": ")
                    .append(msg.getContent())
                    .append("\n");
        }

        // Create summarization prompt
        String summaryPrompt = String.format(
                "Summarize the following customer support conversation in 2-3 sentences, " +
                        "highlighting key issues and resolutions:\n\n%s",
                conversation.toString()
        );

        try {
            String summary = groqService.generateContent(summaryPrompt);
            session.setConversationSummary(summary);
            chatSessionRepository.save(session);
            log.info("Generated summary for session: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to generate summary for session {}: {}", sessionId, e.getMessage());
        }
    }

    /**
     * Schedule summarization (async)
     */
    private void scheduleConversationSummarization(String sessionId) {
        // In a real implementation, use @Async or message queue
        log.info("Scheduling summarization for session: {}", sessionId);
        // For now, just log - implement async execution separately
    }

    /**
     * Scheduled cleanup job - runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredSessions() {
        log.info("Starting scheduled cleanup of expired sessions");

        LocalDateTime now = LocalDateTime.now();

        // 1. Deactivate expired sessions
        int deactivated = chatSessionRepository.deactivateExpiredSessions(now);
        log.info("Deactivated {} expired sessions", deactivated);

        // 2. Delete old messages (older than retention period)
        LocalDateTime cutoffDate = now.minusDays(CLEANUP_RETENTION_DAYS);
        int deletedMessages = chatHistoryRepository.deleteOldMessages(cutoffDate);
        log.info("Deleted {} old messages", deletedMessages);

        // 3. Find and clean up inactive sessions
        LocalDateTime inactiveCutoff = now.minusDays(SESSION_TTL_DAYS);
        List<ChatSession> inactiveSessions = chatSessionRepository
                .findInactiveSessions(inactiveCutoff);

        for (ChatSession session : inactiveSessions) {
            session.setActive(false);
            chatSessionRepository.save(session);
        }
        log.info("Marked {} inactive sessions", inactiveSessions.size());

        log.info("Cleanup completed");
    }

    /**
     * Get session statistics
     */
    public SessionStatistics getSessionStatistics(String sessionId) {
        ChatSession session = getSessionMetadata(sessionId);
        long messageCount = chatHistoryRepository.countBySessionId(sessionId);

        return SessionStatistics.builder()
                .sessionId(sessionId)
                .messageCount(messageCount)
                .createdAt(session.getCreatedAt())
                .lastActivityAt(session.getLastActivityAt())
                .expiresAt(session.getExpiresAt())
                .active(session.getActive())
                .conversationSummary(session.getConversationSummary())
                .build();
    }

    /**
     * Inner class for session statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class SessionStatistics {
        private String sessionId;
        private long messageCount;
        private LocalDateTime createdAt;
        private LocalDateTime lastActivityAt;
        private LocalDateTime expiresAt;
        private Boolean active;
        private String conversationSummary;
    }
}