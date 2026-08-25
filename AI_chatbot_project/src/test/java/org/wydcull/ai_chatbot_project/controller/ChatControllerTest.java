package org.wydcull.ai_chatbot_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.wydcull.ai_chatbot_project.exception.RateLimitExceededException;
import org.wydcull.ai_chatbot_project.exception.SessionNotFoundException;
import org.wydcull.ai_chatbot_project.model.ChatMessage;
import org.wydcull.ai_chatbot_project.model.ChatRequest;
import org.wydcull.ai_chatbot_project.model.ChatResponse;
import org.wydcull.ai_chatbot_project.service.ChatHistoryService;
import org.wydcull.ai_chatbot_project.service.ChatService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ChatService chatService;
    @MockBean private ChatHistoryService chatHistoryService;

    @Test
    void health_returnsUp() throws Exception {
        mockMvc.perform(get("/api/chat/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("AI Chatbot API"));
    }

    @Test
    void sendMessage_validRequest_returns200() throws Exception {
        ChatResponse response = new ChatResponse(
                "session-1", "Hello from ShopEasy", LocalDateTime.now());

        when(chatService.chat("session-1", "Hello")).thenReturn(response);

        ChatRequest request = new ChatRequest("session-1", "Hello");

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-1"))
                .andExpect(jsonPath("$.reply").value("Hello from ShopEasy"));
    }

    @Test
    void sendMessage_emptyMessage_returns400ValidationError() throws Exception {
        ChatRequest request = new ChatRequest("session-1", "");

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors.message").exists())
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    void sendMessage_invalidSessionId_returns400() throws Exception {
        ChatRequest request = new ChatRequest("bad@session#1", "Hello");

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors.sessionId").exists());
    }

    @Test
    void sendMessage_missingSessionId_returns400() throws Exception {
        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Hello\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void sendMessage_rateLimited_returns429() throws Exception {
        when(chatService.chat(anyString(), anyString()))
                .thenThrow(new RateLimitExceededException(
                        "AI service rate limit exceeded. Please try again in a moment."));

        ChatRequest request = new ChatRequest("session-1", "Hello");

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.errorCode").value("RATE_LIMIT_EXCEEDED"));
    }

    @Test
    void getSessionInfo_notFound_returns404() throws Exception {
        when(chatHistoryService.getSessionStatistics("missing"))
                .thenThrow(new SessionNotFoundException("missing"));

        mockMvc.perform(get("/api/chat/session/missing/info"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("SESSION_NOT_FOUND"));
    }

    @Test
    void getRecentMessages_returnsList() throws Exception {
        ChatMessage msg = new ChatMessage("session-1", ChatMessage.Role.USER, "Hi");
        when(chatHistoryService.getRecentMessagesForContext("session-1", 10))
                .thenReturn(List.of(msg));

        mockMvc.perform(get("/api/chat/history/session-1/recent").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hi"))
                .andExpect(jsonPath("$[0].role").value("USER"));
    }

    @Test
    void clearHistory_returnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/chat/history/session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Chat history cleared successfully"));
    }

    @Test
    void summarize_sessionNotFound_returns404() throws Exception {
        doThrow(new SessionNotFoundException("gone"))
                .when(chatHistoryService).generateConversationSummary("gone");

        mockMvc.perform(post("/api/chat/session/gone/summarize"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("SESSION_NOT_FOUND"));
    }
}