package org.wydcull.ai_chatbot_project.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wydcull.ai_chatbot_project.dto.PaginatedResponse;
import org.wydcull.ai_chatbot_project.model.ChatMessage;
import org.wydcull.ai_chatbot_project.model.ChatRequest;
import org.wydcull.ai_chatbot_project.model.ChatResponse;
import org.wydcull.ai_chatbot_project.service.ChatHistoryService;
import org.wydcull.ai_chatbot_project.service.ChatService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final ChatHistoryService chatHistoryService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "AI Chatbot API"
        ));
    }

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request) {
        log.info("📩 Received chat message - Session: {}, MessageLength: {} chars",
                request.getSessionId(),
                request.getMessage().length());

        MDC.put("sessionId", request.getSessionId());

        try {
            ChatResponse response = chatService.chat(request.getSessionId(), request.getMessage());

            log.info("✅ Chat response sent - Session: {}, ResponseLength: {} chars",
                    request.getSessionId(),
                    response.getReply().length());

            return ResponseEntity.ok(response);

        } finally {
            MDC.remove("sessionId");
        }
    }

    // NEW: Paginated history endpoint
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<PaginatedResponse<ChatMessage>> getHistory(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        log.info("📖 Fetching history - Session: {}, Page: {}, Size: {}", sessionId, page, size);

        PaginatedResponse<ChatMessage> history = chatHistoryService.getHistory(sessionId, page, size);

        log.info("✅ History retrieved - Session: {}, TotalMessages: {}, Pages: {}",
                sessionId, history.getTotalElements(), history.getTotalPages());

        return ResponseEntity.ok(history);
    }


    // NEW: Get recent messages (for UI quick view)
    @GetMapping("/history/{sessionId}/recent")
    public ResponseEntity<List<ChatMessage>> getRecentMessages(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {

        log.info("Fetching {} recent messages for session: {}", limit, sessionId);

        List<ChatMessage> messages = chatHistoryService.getRecentMessagesForContext(sessionId, limit);
        return ResponseEntity.ok(messages);
    }

    // NEW: Get session metadata and statistics
    @GetMapping("/session/{sessionId}/info")
    public ResponseEntity<ChatHistoryService.SessionStatistics> getSessionInfo(
            @PathVariable String sessionId) {

        log.info("Fetching session info for: {}", sessionId);

        ChatHistoryService.SessionStatistics stats = chatHistoryService.getSessionStatistics(sessionId);
        return ResponseEntity.ok(stats);
    }

    // NEW: Generate conversation summary
    @PostMapping("/session/{sessionId}/summarize")
    public ResponseEntity<Map<String, String>> summarizeConversation(
            @PathVariable String sessionId) {

        log.info("Generating summary for session: {}", sessionId);

        chatHistoryService.generateConversationSummary(sessionId);

        return ResponseEntity.ok(Map.of(
                "message", "Conversation summary generated successfully"
        ));
    }

    // NEW: Extend session TTL
    @PostMapping("/session/{sessionId}/extend")
    public ResponseEntity<Map<String, String>> extendSession(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "30") int days) {

        log.info("Extending session {} by {} days", sessionId, days);

        chatHistoryService.extendSessionTTL(sessionId, days);

        return ResponseEntity.ok(Map.of(
                "message", String.format("Session extended by %d days", days)
        ));
    }

    @DeleteMapping("/history/{sessionId}")
    public ResponseEntity<Map<String, String>> clearHistory(@PathVariable String sessionId) {
        log.info("Clearing history for session: {}", sessionId);
        chatHistoryService.clearHistory(sessionId);
        return ResponseEntity.ok(Map.of("message", "Chat history cleared successfully"));
    }

    // NEW: Delete entire session
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, String>> deleteSession(@PathVariable String sessionId) {
        log.info("Deleting session: {}", sessionId);
        chatHistoryService.deleteSession(sessionId);
        return ResponseEntity.ok(Map.of("message", "Session deleted successfully"));
    }
}