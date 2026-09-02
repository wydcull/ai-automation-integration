package com.rag.AIrag.service;

import com.google.common.util.concurrent.RateLimiter;
import com.rag.AIrag.dto.groq.GroqRequest;
import com.rag.AIrag.dto.groq.GroqResponse;
import com.rag.AIrag.exception.AIServiceException;
import com.rag.AIrag.exception.RateLimitExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@Slf4j
public class GroqService {

    private final WebClient webClient;
    private final String model;
    private final double temperature;
    private final int maxTokens;

    private final RateLimiter rateLimiter = RateLimiter.create(30.0 / 60.0);

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    public GroqService(
            @Value("${groq.api.base-url}") String baseUrl,
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.api.model}") String model,
            @Value("${groq.api.temperature}") double temperature,
            @Value("${groq.api.max-tokens}") int maxTokens) {

        this.model = model;
        this.temperature = temperature;
        this.maxTokens = maxTokens;

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        log.info("GroqService initialized with model: {}", model);
    }

    // Simple single-prompt call
    public String generateContent(String prompt) {
        rateLimiter.acquire();
        GroqRequest.Message message = new GroqRequest.Message("user", prompt);
        return executeWithRetryMessages(List.of(message), 0);
    }

    // RAG uses this — system prompt + user question
    public String generateContentWithMessages(List<GroqRequest.Message> messages) {
        rateLimiter.acquire();
        return executeWithRetryMessages(messages, 0);
    }

    private String executeWithRetryMessages(List<GroqRequest.Message> messages, int attemptNumber) {
        try {
            GroqRequest request = new GroqRequest(
                    model, messages, temperature, maxTokens, "parsed", "none");

            GroqResponse response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GroqResponse.class)
                    .block();

            if (response != null) {
                String text = response.getGeneratedText();
                log.debug("Groq response: {}", text);
                return text;
            }

            return "Sorry, I couldn't generate a response.";

        } catch (WebClientResponseException.TooManyRequests e) {
            return handleRateLimitError(messages, attemptNumber);

        } catch (WebClientResponseException e) {
            log.error("Groq API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AIServiceException("Failed to call Groq API: " + e.getResponseBodyAsString(), e);

        } catch (Exception e) {
            log.error("Groq API error: {}", e.getMessage(), e);
            throw new AIServiceException("Failed to call Groq API: " + e.getMessage(), e);
        }
    }

    private String handleRateLimitError(List<GroqRequest.Message> messages, int attemptNumber) {
        if (attemptNumber >= MAX_RETRY_ATTEMPTS - 1) {
            throw new RateLimitExceededException(
                    "AI service rate limit exceeded. Please try again in a moment.");
        }

        long delayMs = INITIAL_RETRY_DELAY_MS * (long) Math.pow(2, attemptNumber);
        log.info("Rate limited. Retrying in {} ms...", delayMs);

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new AIServiceException("Retry interrupted", ie);
        }

        return executeWithRetryMessages(messages, attemptNumber + 1);
    }
}