package org.wydcull.ai_chatbot_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    private final ChatHistoryService chatHistoryService;
    private final EcommerceService ecommerceService;
    private final ChatHistoryRepository repository;

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
    - Use a friendly, conversational tone
    
    RESPONSE FORMAT RULES:
    - For MULTIPLE products: use a short intro line, then a bullet list (one product per bullet)
    - For EACH product bullet include: Name, Price, 1-line description, Stock status
    - Do NOT put multiple products into one long paragraph
    - For a SINGLE product: use a short intro + clear labeled lines (Name, Price, Category, Stock)
    - For orders: use labeled lines (Order Number, Status, Tracking, Estimated Delivery)
    - Keep the intro to 1 sentence, then structured details
    - End with one short follow-up question when helpful
    
    PRODUCT RESPONSE EXAMPLE:
    I found 2 headphones for you:
    
    • Wireless Headphones — $299.99
      Premium noise-cancelling with 30-hour battery. In Stock.
    
    • Sony WH-1000XM5 Headphones — $349.99
      Premium noise-canceling features. In Stock.
    
    Would you like more details on either one?
    """;

    public ChatResponse chat(String sessionId, String userMessage) {
        long startTime = System.currentTimeMillis();

        // Add session ID to MDC for all logs in this thread
        MDC.put("sessionId", sessionId);

        log.info("=== CHAT REQUEST START === Session: {}, MessageLength: {}",
                sessionId, userMessage.length());

        try {
            // Validate inputs
            if (sessionId == null || sessionId.trim().isEmpty()) {
                log.error("Invalid session ID provided: '{}'", sessionId);
                throw new InvalidRequestException("sessionId", "cannot be empty");
            }

            if (userMessage == null || userMessage.trim().isEmpty()) {
                log.error("Invalid message provided for session: {}", sessionId);
                throw new InvalidRequestException("message", "cannot be empty");
            }

            log.debug("User message: '{}'", userMessage);

            // 1. Save user message
            log.debug("Step 1: Saving user message to database");
            chatHistoryService.saveMessage(sessionId, ChatMessage.Role.USER, userMessage);

            // 2. Load conversation history
            log.debug("Step 2: Loading conversation history");
            List<ChatMessage> history = chatHistoryService.getRecentMessagesForContext(sessionId, 10);
            log.info("Loaded {} historical messages for context", history.size());

            // 3. Get session statistics
            log.debug("Step 3: Fetching session statistics");
            ChatHistoryService.SessionStatistics stats = chatHistoryService.getSessionStatistics(sessionId);
            log.info("Session stats - Total messages: {}, Created: {}",
                    stats.getMessageCount(), stats.getCreatedAt());

            String conversationContext = "";
            if (stats.getConversationSummary() != null && stats.getMessageCount() > 20) {
                conversationContext = "\n\nPrevious conversation summary: " + stats.getConversationSummary();
                log.debug("Using conversation summary for long chat history");
            }

            // 4. Extract context from database
            log.debug("Step 4: Extracting relevant data from database");
            long extractStart = System.currentTimeMillis();
            String contextData = extractRelevantData(userMessage);
            long extractDuration = System.currentTimeMillis() - extractStart;
            log.info("Context extraction completed in {}ms, found {} characters of data",
                    extractDuration, contextData.length());

            // 5. Build messages with context
            log.debug("Step 5: Building AI prompt with context");
            List<GroqRequest.Message> messages = buildMessagesWithContext(
                    history,
                    contextData,
                    conversationContext
            );
            log.info("Built prompt with {} messages", messages.size());

            // 6. Call Groq API
            log.info("Step 6: Calling Groq API...");
            long apiStart = System.currentTimeMillis();
            String aiReply = groqService.generateContentWithMessages(messages);
            long apiDuration = System.currentTimeMillis() - apiStart;

            if (aiReply == null || aiReply.trim().isEmpty()) {
                log.error("AI service returned empty response for session: {}", sessionId);
                throw new AIServiceException("AI service returned empty response");
            }

            log.info("Groq API call completed in {}ms, response length: {} characters",
                    apiDuration, aiReply.length());
            log.debug("AI response preview: '{}'",
                    aiReply.length() > 100 ? aiReply.substring(0, 100) + "..." : aiReply);

            // 7. Save AI response
            log.debug("Step 7: Saving AI response to database");
            chatHistoryService.saveMessage(sessionId, ChatMessage.Role.ASSISTANT, aiReply);

            long totalDuration = System.currentTimeMillis() - startTime;
            log.info("=== CHAT REQUEST END === Session: {}, Total Duration: {}ms, " +
                            "Context: {}ms, API: {}ms",
                    sessionId, totalDuration, extractDuration, apiDuration);

            return new ChatResponse(sessionId, aiReply, LocalDateTime.now());

        } catch (ChatbotException e) {
            log.warn("Chat processing failed with known exception: {} - {}",
                    e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing chat for session: {}", sessionId, e);
            throw new AIServiceException("Failed to process chat message", e);
        } finally {
            MDC.remove("sessionId");
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
        // Check for product queries - NEW IMPROVED VERSION
        else if (isProductQuery(lowerMessage)) {
            log.info("Detected product query: {}", userMessage);

            // NEW: Use smart search instead of hardcoded keywords
            List<Product> products = ecommerceService.searchProducts(userMessage);

            if (!products.isEmpty()) {
                context.append("\n\nFound ").append(products.size())
                        .append(" matching product(s):\n\n");

                // Show top 3 most relevant products
                products.stream()
                        .limit(3)
                        .forEach(product -> context.append(formatProductInfo(product)));

                if (products.size() > 3) {
                    context.append("\n... and ").append(products.size() - 3)
                            .append(" more products available.\n");
                }
            } else {
                // Fallback: show available products if no match found
                List<Product> allProducts = ecommerceService.getInStockProducts();

                if (!allProducts.isEmpty()) {
                    context.append("\n\nI couldn't find products matching '")
                            .append(userMessage)
                            .append("', but here are our available products:\n\n");

                    allProducts.stream()
                            .limit(5)
                            .forEach(p -> context.append(String.format(
                                    "- %s ($%.2f) - %s\n",
                                    p.getName(),
                                    p.getPrice(),
                                    p.getCategory()
                            )));
                } else {
                    context.append("\n\nProduct Status: NOT FOUND - No products match your query.\n");
                }
            }
        }

        log.debug("Extracted context data: {}", context.toString());
        return context.toString();
    }

    /**
     * Check if message is a product query
     */
    private boolean isProductQuery(String lowerMessage) {
        // Check for common product-related words
        String[] productIndicators = {
                "product", "item", "buy", "purchase", "looking for",
                "show me", "what", "do you have", "available", "price",
                "cost", "how much", "sell", "stock", "laptop", "phone",
                "headphones", "watch", "tv", "sony", "samsung", "apple"
        };

        for (String indicator : productIndicators) {
            if (lowerMessage.contains(indicator)) {
                return true;
            }
        }

        // Check if message contains product keywords
        List<String> keywords = ecommerceService.extractProductKeywords(lowerMessage);
        return !keywords.isEmpty();
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

    private List<GroqRequest.Message> buildMessagesWithContext(
            List<ChatMessage> history,
            String contextData,
            String conversationContext) {

        List<GroqRequest.Message> messages = new ArrayList<>();
        String enhancedSystemPrompt = SYSTEM_PROMPT + conversationContext;
        if (!contextData.isEmpty()) {
            enhancedSystemPrompt += "\n\nRelevant data from database:" + contextData;
        } else {
            enhancedSystemPrompt += "\n\nRelevant data from database: No specific product or order data found for this query.";
        }
        messages.add(new GroqRequest.Message("system", enhancedSystemPrompt));
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