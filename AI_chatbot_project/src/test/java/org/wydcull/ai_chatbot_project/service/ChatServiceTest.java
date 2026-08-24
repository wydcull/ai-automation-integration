package org.wydcull.ai_chatbot_project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wydcull.ai_chatbot_project.dto.groq.GroqRequest;
import org.wydcull.ai_chatbot_project.exception.AIServiceException;
import org.wydcull.ai_chatbot_project.exception.InvalidRequestException;
import org.wydcull.ai_chatbot_project.model.ChatMessage;
import org.wydcull.ai_chatbot_project.model.ChatResponse;
import org.wydcull.ai_chatbot_project.model.Order;
import org.wydcull.ai_chatbot_project.model.Product;
import org.wydcull.ai_chatbot_project.repository.ChatHistoryRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private GroqService groqService;
    @Mock private ChatHistoryService chatHistoryService;
    @Mock private EcommerceService ecommerceService;
    @Mock private ChatHistoryRepository repository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void chat_blankSessionId_throwsInvalidRequest() {
        assertThatThrownBy(() -> chatService.chat("  ", "Hello"))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("sessionId");
    }

    @Test
    void chat_blankMessage_throwsInvalidRequest() {
        assertThatThrownBy(() -> chatService.chat("session-1", "   "))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("message");
    }

    @Test
    void chat_success_savesUserAndAssistantMessages() {
        String sessionId = "session-1";
        String userMsg = "What is your return policy?";
        String aiReply = "We have a 7-day return window.";

        when(chatHistoryService.getRecentMessagesForContext(sessionId, 10))
                .thenReturn(Collections.emptyList());
        when(chatHistoryService.getSessionStatistics(sessionId))
                .thenReturn(ChatHistoryService.SessionStatistics.builder()
                        .sessionId(sessionId)
                        .messageCount(0)
                        .createdAt(LocalDateTime.now())
                        .build());
        when(groqService.generateContentWithMessages(anyList())).thenReturn(aiReply);

        ChatResponse response = chatService.chat(sessionId, userMsg);

        assertThat(response.getSessionId()).isEqualTo(sessionId);
        assertThat(response.getReply()).isEqualTo(aiReply);
        assertThat(response.getTimestamp()).isNotNull();

        verify(chatHistoryService).saveMessage(sessionId, ChatMessage.Role.USER, userMsg);
        verify(chatHistoryService).saveMessage(sessionId, ChatMessage.Role.ASSISTANT, aiReply);
    }

    @Test
    void chat_emptyAiReply_throwsAIServiceException() {
        when(chatHistoryService.getRecentMessagesForContext(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(chatHistoryService.getSessionStatistics(anyString()))
                .thenReturn(ChatHistoryService.SessionStatistics.builder()
                        .sessionId("s1").messageCount(0).build());
        when(groqService.generateContentWithMessages(anyList())).thenReturn("  ");

        assertThatThrownBy(() -> chatService.chat("s1", "Hello"))
                .isInstanceOf(AIServiceException.class)
                .hasMessageContaining("empty response");
    }

    @Test
    void chat_orderLookup_includesOrderContextInPrompt() {
        Order order = new Order();
        order.setOrderNumber("ORD-12345");
        order.setStatus(Order.OrderStatus.SHIPPED);
        order.setTotalAmount(new BigDecimal("150.00"));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingMethod(Order.ShippingMethod.STANDARD);
        order.setShippingAddress("123 Main St");
        order.setItems(Collections.emptyList());

        when(chatHistoryService.getRecentMessagesForContext(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(chatHistoryService.getSessionStatistics(anyString()))
                .thenReturn(ChatHistoryService.SessionStatistics.builder()
                        .sessionId("s1").messageCount(0).build());
        when(ecommerceService.findOrderByNumber("ORD-12345")).thenReturn(Optional.of(order));
        when(groqService.generateContentWithMessages(anyList())).thenReturn("Your order is shipped.");

        chatService.chat("s1", "Track order ORD-12345");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GroqRequest.Message>> captor = ArgumentCaptor.forClass(List.class);
        verify(groqService).generateContentWithMessages(captor.capture());

        String systemPrompt = captor.getValue().get(0).getContent();
        assertThat(systemPrompt).contains("ORD-12345");
        assertThat(systemPrompt).contains("SHIPPED");
    }

    @Test
    void chat_productQuery_usesSmartSearch() {
        Product p = new Product();
        p.setName("Gaming Laptop");
        p.setPrice(new BigDecimal("999"));
        p.setCategory("Electronics");
        p.setDescription("Fast laptop");
        p.setStockQuantity(3);
        p.setSku("L1");
        p.setInStock(true);

        when(chatHistoryService.getRecentMessagesForContext(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(chatHistoryService.getSessionStatistics(anyString()))
                .thenReturn(ChatHistoryService.SessionStatistics.builder()
                        .sessionId("s1").messageCount(0).build());
        when(ecommerceService.searchProducts(anyString())).thenReturn(List.of(p));
        when(groqService.generateContentWithMessages(anyList())).thenReturn("We have laptops.");

        chatService.chat("s1", "Do you have laptops?");

        verify(ecommerceService).searchProducts("Do you have laptops?");
    }
}
