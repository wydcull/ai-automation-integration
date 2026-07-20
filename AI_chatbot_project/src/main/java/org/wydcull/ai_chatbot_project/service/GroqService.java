package org.wydcull.ai_chatbot_project.service;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.wydcull.ai_chatbot_project.dto.groq.GroqRequest;
import org.wydcull.ai_chatbot_project.dto.groq.GroqResponse;

import java.util.List;

@Service
@Slf4j
public class GroqService {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final double temperature;
    private final int maxTokens;

    // Rate limiter: 30 requests per minute for Groq
    private final RateLimiter rateLimiter = RateLimiter.create(30.0 / 60.0);

    // Retry configuration
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    public GroqService(
            @Value("${groq.api.base-url}") String baseUrl,
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.api.model}") String model,
            @Value("${groq.api.temperature}") double temperature,
            @Value("${groq.api.max-tokens}") int maxTokens) {

        this.apiKey = apiKey;
        this.model = model;
        this.temperature = temperature;
        this.maxTokens = maxTokens;

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        log.info("GroqService initialized with model: {} and rate limit: 30 RPM", model);
    }

    @Cacheable(value = "groqResponses", key = "#prompt")
    public String generateContent(String prompt) {
        log.debug("Sending request to Groq API with model: {}", model);

        // Apply rate limiting
        rateLimiter.acquire();
        log.debug("Rate limiter acquired, proceeding with request");

        // Retry logic with exponential backoff
        return executeWithRetry(prompt, 0);
    }

    public String generateContentWithMessages(List<GroqRequest.Message> messages) {
        log.debug("Sending messages request to Groq API with model: {}", model);

        // Apply rate limiting
        rateLimiter.acquire();
        log.debug("Rate limiter acquired, proceeding with request");

        // Retry logic with exponential backoff
        return executeWithRetryMessages(messages, 0);
    }

    private String executeWithRetry(String prompt, int attemptNumber) {
        GroqRequest.Message message = new GroqRequest.Message("user", prompt);
        return executeWithRetryMessages(List.of(message), attemptNumber);
    }

    private String executeWithRetryMessages(List<GroqRequest.Message> messages, int attemptNumber) {
        try {
            GroqRequest request = buildRequest(messages);

 // Add this logging
        log.debug("Request being sent to Groq:");
        log.debug("Model: {}", request.getModel());
        log.debug("Temperature: {}", request.getTemperature());
        log.debug("Max tokens: {}", request.getMax_tokens());
        log.debug("Number of messages: {}", request.getMessages().size());
        messages.forEach(msg -> log.debug("  - {}: {}", msg.getRole(), 
            msg.getContent().length() > 100 ? msg.getContent().substring(0, 100) + "..." : msg.getContent()));
            
            GroqResponse response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GroqResponse.class)
                    .block();

            if (response != null) {
    log.debug("Full Groq Response: {}", response);  // Add this line
    log.debug("Choices: {}", response.getChoices());  // Add this line
    
    String generatedText = response.getGeneratedText();
    log.debug("Received response from Groq: {}", generatedText);
    
    // Check finish_reason
    if (response.getChoices() != null && !response.getChoices().isEmpty()) {
        String finishReason = response.getChoices().get(0).getFinish_reason();
        log.debug("Finish reason: {}", finishReason);  // Add this line
    }
    
    // Log token usage if available
    if (response.getUsage() != null) {
        log.debug("Token usage - Prompt: {}, Completion: {}, Total: {}",
                response.getUsage().getPrompt_tokens(),
                response.getUsage().getCompletion_tokens(),
                response.getUsage().getTotal_tokens());
    }
    
    return generatedText;
}

            return "Sorry, I couldn't generate a response.";

        } catch (WebClientResponseException.TooManyRequests e) {
            log.warn("Rate limit exceeded (429), attempt {}/{}", attemptNumber + 1, MAX_RETRY_ATTEMPTS);
            return handleRateLimitError(messages, attemptNumber, e);

        } catch (WebClientResponseException e) {
            log.error("HTTP error calling Groq API: {} - {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to call Groq API: " + e.getMessage(), e);

        } catch (Exception e) {
            log.error("Error calling Groq API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call Groq API: " + e.getMessage(), e);
        }
    }

    private String handleRateLimitError(List<GroqRequest.Message> messages, int attemptNumber, Exception originalException) {
        if (attemptNumber >= MAX_RETRY_ATTEMPTS - 1) {
            log.error("Max retry attempts reached. Giving up.");
            throw new RuntimeException("Rate limit exceeded after " + MAX_RETRY_ATTEMPTS + " attempts", originalException);
        }

        // Exponential backoff: 1s, 2s, 4s, 8s...
        long delayMs = INITIAL_RETRY_DELAY_MS * (long) Math.pow(2, attemptNumber);
        log.info("Retrying in {} ms...", delayMs);

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", ie);
        }

        return executeWithRetryMessages(messages, attemptNumber + 1);
    }

    private GroqRequest buildRequest(List<GroqRequest.Message> messages) {
        return new GroqRequest(model, messages, temperature, maxTokens);
    }
}