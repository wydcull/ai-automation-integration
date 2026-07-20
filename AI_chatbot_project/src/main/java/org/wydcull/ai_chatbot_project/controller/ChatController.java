package org.wydcull.ai_chatbot_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wydcull.ai_chatbot_project.model.ChatMessage;
import org.wydcull.ai_chatbot_project.model.ChatRequest;
import org.wydcull.ai_chatbot_project.model.ChatResponse;
import org.wydcull.ai_chatbot_project.service.ChatService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // Allow all origins for development
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "AI Chatbot API"
        ));
    }

    // Send a chat message
    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat request for session: {}", request.getSessionId());

        try {
            ChatResponse response = chatService.chat(
                    request.getSessionId(),
                    request.getMessage()
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error handling chat request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get chat history for a session
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable String sessionId) {
        log.info("Fetching history for session: {}", sessionId);

        try {
            List<ChatMessage> history = chatService.getHistory(sessionId);
            return ResponseEntity.ok(history);

        } catch (Exception e) {
            log.error("Error fetching history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Clear chat history for a session
    @DeleteMapping("/history/{sessionId}")
    public ResponseEntity<Map<String, String>> clearHistory(@PathVariable String sessionId) {
        log.info("Clearing history for session: {}", sessionId);

        try {
            chatService.clearHistory(sessionId);
            return ResponseEntity.ok(Map.of("message", "Chat history cleared successfully"));

        } catch (Exception e) {
            log.error("Error clearing history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}