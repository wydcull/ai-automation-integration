package org.wydcull.ai_chatbot_project.service;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.wydcull.ai_chatbot_project.dto.gemini.GeminiRequest;
import org.wydcull.ai_chatbot_project.dto.gemini.GeminiResponse;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class GeminiService {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final double temperature;
    private final int maxTokens;

    // Rate limiter: 12 requests per minute (leaving buffer under 15 limit)
    private final RateLimiter rateLimiter = RateLimiter.create(12.0 / 60.0);

    // Retry configuration
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    public GeminiService(
            @Value("${gemini.api.base-url}") String baseUrl,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.model}") String model,
            @Value("${gemini.api.temperature}") double temperature,
            @Value("${gemini.api.max-tokens}") int maxTokens) {

        this.apiKey = apiKey;
        this.model = model;
        this.temperature = temperature;
        this.maxTokens = maxTokens;

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        log.info("GeminiService initialized with model: {} and rate limit: 12 RPM", model);
    }

    @Cacheable(value = "geminiResponses", key = "#prompt")
    public String generateContent(String prompt) {
        log.debug("Sending request to Gemini API with model: {}", model);

        // Apply rate limiting
        rateLimiter.acquire();
        log.debug("Rate limiter acquired, proceeding with request");

        // Retry logic with exponential backoff
        return executeWithRetry(prompt, 0);
    }

    private String executeWithRetry(String prompt, int attemptNumber) {
        try {
            GeminiRequest request = buildRequest(prompt);
            String endpoint = String.format("/models/%s:generateContent", model);

            GeminiResponse response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(endpoint)
                            .queryParam("key", apiKey)
                            .build())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block();

            if (response != null) {
                String generatedText = response.getGeneratedText();
                log.debug("Received response from Gemini: {}", generatedText);
                return generatedText;
            }

            return "Sorry, I couldn't generate a response.";

        } catch (WebClientResponseException.TooManyRequests e) {
            log.warn("Rate limit exceeded (429), attempt {}/{}", attemptNumber + 1, MAX_RETRY_ATTEMPTS);
            return handleRateLimitError(prompt, attemptNumber, e);

        } catch (WebClientResponseException e) {
            log.error("HTTP error calling Gemini API: {} - {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);

        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
    }

    private String handleRateLimitError(String prompt, int attemptNumber, Exception originalException) {
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

        return executeWithRetry(prompt, attemptNumber + 1);
    }

    private GeminiRequest buildRequest(String prompt) {
        GeminiRequest.Content content = new GeminiRequest.Content(prompt);
        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig(
                temperature,
                maxTokens
        );

        return new GeminiRequest(List.of(content), config);
    }
}