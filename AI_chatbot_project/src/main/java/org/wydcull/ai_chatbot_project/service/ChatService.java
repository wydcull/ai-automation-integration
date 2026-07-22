package org.wydcull.ai_chatbot_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wydcull.ai_chatbot_project.dto.groq.GroqRequest;
import org.wydcull.ai_chatbot_project.exception.AIServiceException;
import org.wydcull.ai_chatbot_project.exception.ChatbotException;
import org.wydcull.ai_chatbot_project.exception.InvalidRequestException;
import org.wydcull.ai_chatbot_project.model.*;
import org.wydcull.ai_chatbot_project.repository.ChatHistoryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final GroqService groqService;
    private final ChatHistoryRepository repository;
    private final EcommerceService ecommerceService;

    private static final String SYSTEM_PROMPT = """
    You are a helpful and friendly customer support assistant for an e-commerce store called "ShopEasy".
    
    CRITICAL: Always provide a helpful response, even if the user asks the same question multiple times. 
    Users may need reassurance or may have forgotten previous answers.
    
    You can ONLY provide information about products and orders that are explicitly provided to you in the "Relevant data from database" section.
    
    Your responsibilities:
    1. Help customers with order tracking and status inquiries
    2. Explain return and refund policies (7-day return window for unused items)
    3. Provide product information and recommendations ONLY from the provided database
    4. Answer shipping questions (Standard: 3-5 business days, Express: 1-2 business days)
    5. Assist with payment and checkout issues
    
    Guidelines:
    - Be polite, professional, and empathetic
    - Always answer the user's question, even if it's repetitive
    - When provided with specific data (products, orders), use it accurately in your response
    - If a product or order is NOT in the provided database information, clearly say "I couldn't find that product/order in our system"
    - NEVER make up product details, prices, or specifications
    - If you don't have specific information, acknowledge it and offer to help find alternatives
    - Keep responses concise (2-3 sentences)
    - Use a friendly, conversational tone
    """;

    public ChatResponse chat(String sessionId, String userMessage) {
        log.info("Processing chat for session: {}, message: {}", sessionId, userMessage);

        // Validate inputs
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new InvalidRequestException("sessionId", "cannot be empty");
        }

        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new InvalidRequestException("message", "cannot be empty");
        }

        if (userMessage.length() > 1000) {
            throw new InvalidRequestException("message", "exceeds maximum length of 1000 characters");
        }

        try {
            // 1. Save user message
            ChatMessage userChatMessage = new ChatMessage(sessionId, ChatMessage.Role.USER, userMessage);
            repository.save(userChatMessage);

            // 2. Load conversation history
            List<ChatMessage> history = repository.findTop5BySessionIdOrderByCreatedAtAsc(sessionId);
            log.debug("Loaded {} historical messages", history.size());

            // 3. Extract context from database
            String contextData = extractRelevantData(userMessage);

            // 4. Build messages with context
            List<GroqRequest.Message> messages = buildMessagesWithContext(history, contextData);

            // 5. Call Groq API
            String aiReply = groqService.generateContentWithMessages(messages);

            if (aiReply == null || aiReply.trim().isEmpty()) {
                throw new AIServiceException("AI service returned empty response");
            }

            // 6. Save AI response
            ChatMessage assistantMessage = new ChatMessage(sessionId, ChatMessage.Role.ASSISTANT, aiReply);
            repository.save(assistantMessage);

            return new ChatResponse(sessionId, aiReply, LocalDateTime.now());

        } catch (ChatbotException e) {
            // Re-throw custom exceptions
            throw e;
        } catch (Exception e) {
            log.error("Error processing chat: {}", e.getMessage(), e);
            throw new AIServiceException("Failed to process chat message", e);
        }
    }

    private String extractRelevantData(String userMessage) {
        StringBuilder context = new StringBuilder();
        String lowerMessage = userMessage.toLowerCase();

        // Check for order tracking requests
        Pattern orderPattern = Pattern.compile("(ORD-\\d+)", Pattern.CASE_INSENSITIVE);
        Pattern trackingPattern = Pattern.compile("(TRK-[A-Z0-9]+)", Pattern.CASE_INSENSITIVE);

        Matcher orderMatcher = orderPattern.matcher(userMessage);
        Matcher trackingMatcher = trackingPattern.matcher(userMessage);

        if (orderMatcher.find()) {
            String orderNumber = orderMatcher.group(1).toUpperCase();
            log.info("Detected order lookup: {}", orderNumber);

            Optional<Order> order = ecommerceService.findOrderByNumber(orderNumber);
            if (order.isPresent()) {
                context.append(formatOrderInfo(order.get()));
            } else {
                context.append("\n\nOrder Status: NOT FOUND - Order number ").append(orderNumber).append(" does not exist in the system.\n");
                log.warn("Order not found in database: {}", orderNumber);
            }
        } else if (trackingMatcher.find()) {
            String trackingNumber = trackingMatcher.group(1).toUpperCase();
            log.info("Detected tracking lookup: {}", trackingNumber);

            Optional<Order> order = ecommerceService.findOrderByTracking(trackingNumber);
            if (order.isPresent()) {
                context.append(formatOrderInfo(order.get()));
            } else {
                context.append("\n\nTracking Status: NOT FOUND - Tracking number ").append(trackingNumber).append(" does not exist in the system.\n");
                log.warn("Tracking number not found in database: {}", trackingNumber);
            }
        }
        // Check for product queries
        else if (lowerMessage.contains("product") || lowerMessage.contains("item") ||
                lowerMessage.contains("laptop") || lowerMessage.contains("phone") ||
                lowerMessage.contains("headphones") || lowerMessage.contains("watch") ||
                lowerMessage.contains("tv") || lowerMessage.contains("sony") ||
                lowerMessage.contains("samsung") || lowerMessage.contains("apple")) {

            log.info("Detected product query: {}", userMessage);

            // Extract product name from message
            String[] keywords = {"laptop", "phone", "headphones", "watch", "tv", "mouse", "hub", "wireless", "ultra", "gaming", "smart"};
            boolean productFound = false;

            for (String keyword : keywords) {
                if (lowerMessage.contains(keyword)) {
                    Optional<Product> product = ecommerceService.findProductByName(keyword);
                    if (product.isPresent()) {
                        context.append(formatProductInfo(product.get()));
                        productFound = true;
                        break;
                    }
                }
            }

            // If no specific product found by keyword search
            if (!productFound) {
                // Try to list all products that might be relevant
                List<Product> allProducts = ecommerceService.getAllProducts();

                if (!allProducts.isEmpty()) {
                    context.append("\n\nThe specific product mentioned was NOT FOUND in our database.\n");
                    context.append("Here are the products we currently have available:\n");
                    allProducts.forEach(p ->
                            context.append(String.format("- %s: $%.2f (%s) - %s\n",
                                    p.getName(), p.getPrice(),
                                    p.getInStock() ? "In Stock" : "Out of Stock",
                                    p.getCategory()))
                    );
                } else {
                    context.append("\n\nProduct Status: NOT FOUND - The requested product is not available in our catalog.\n");
                }
            }
        }

        log.debug("Extracted context data: {}", context.toString());
        return context.toString();
    }

    private String formatOrderInfo(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        StringBuilder info = new StringBuilder("\n\nOrder Details:\n");
        info.append("Order Number: ").append(order.getOrderNumber()).append("\n");
        info.append("Status: ").append(order.getStatus()).append("\n");
        info.append("Order Date: ").append(order.getOrderDate().format(formatter)).append("\n");
        info.append("Total Amount: $").append(order.getTotalAmount()).append("\n");

        if (order.getTrackingNumber() != null) {
            info.append("Tracking Number: ").append(order.getTrackingNumber()).append("\n");
        }

        if (order.getEstimatedDelivery() != null) {
            info.append("Estimated Delivery: ").append(order.getEstimatedDelivery().format(formatter)).append("\n");
        }

        info.append("Shipping Method: ").append(order.getShippingMethod()).append("\n");
        info.append("Shipping Address: ").append(order.getShippingAddress()).append("\n");

        info.append("\nItems:\n");
        order.getItems().forEach(item ->
                info.append(String.format("- %s (x%d): $%.2f\n",
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getSubtotal()))
        );

        return info.toString();
    }

    private String formatProductInfo(Product product) {
        StringBuilder info = new StringBuilder("\n\nProduct Details:\n");
        info.append("Name: ").append(product.getName()).append("\n");
        info.append("Price: $").append(product.getPrice()).append("\n");
        info.append("Category: ").append(product.getCategory()).append("\n");
        info.append("Description: ").append(product.getDescription()).append("\n");
        info.append("Stock: ").append(product.getInStock() ?
                "In Stock (" + product.getStockQuantity() + " available)" : "Out of Stock").append("\n");
        info.append("SKU: ").append(product.getSku()).append("\n");

        return info.toString();
    }

    private List<GroqRequest.Message> buildMessagesWithContext(List<ChatMessage> history, String contextData) {
        List<GroqRequest.Message> messages = new ArrayList<>();

        // Add system prompt with explicit database context
        String enhancedSystemPrompt = SYSTEM_PROMPT;

        if (!contextData.isEmpty()) {
            enhancedSystemPrompt += "\n\nRelevant data from database:" + contextData;
        } else {
            enhancedSystemPrompt += "\n\nRelevant data from database: No specific product or order data found for this query. Only provide general information about policies or suggest checking our available products.";
        }

        messages.add(new GroqRequest.Message("system", enhancedSystemPrompt));

        // Add conversation history
        for (ChatMessage msg : history) {
            String role = msg.getRole() == ChatMessage.Role.USER ? "user" : "assistant";
            messages.add(new GroqRequest.Message(role, msg.getContent()));
        }

        return messages;
    }

    public List<ChatMessage> getHistory(String sessionId) {
        return repository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    public void clearHistory(String sessionId) {
        List<ChatMessage> messages = repository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        repository.deleteAll(messages);
    }
}