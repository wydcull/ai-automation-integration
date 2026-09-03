package com.rag.AIrag.service;

import com.rag.AIrag.dto.EmbeddingResponse;
import com.rag.AIrag.exception.AIServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmbeddingService {

    private final WebClient webClient;
    private final String model;

    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final long INITIAL_RETRY_DELAY_MS = 2000;
    private static final int BATCH_SIZE = 20;  // embed 20 chunks per API call

    public EmbeddingService(
            @Value("${openai.api.base-url}") String baseUrl,
            @Value("${openai.api.key}") String apiKey,
            @Value("${embedding.model}") String model) {

        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // Single text embed (used for questions at query time)
    public List<Double> embed(String text) {
        return embedBatch(List.of(text)).get(0);
    }

    // Batch embed — multiple chunks in one API call
    public List<List<Double>> embedBatch(List<String> texts) {
        List<List<Double>> allEmbeddings = new ArrayList<>();

        for (int i = 0; i < texts.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, texts.size());
            List<String> batch = texts.subList(i, end);

            log.debug("Embedding batch {}-{} of {}", i, end, texts.size());
            EmbeddingResponse response = callWithRetry(batch);

            for (EmbeddingResponse.Item item : response.getData()) {
                allEmbeddings.add(item.getEmbedding());
            }

            // small pause between batches to avoid rate limit
            if (end < texts.size()) {
                sleep(500);
            }
        }

        return allEmbeddings;
    }

    private EmbeddingResponse callWithRetry(List<String> texts) {
        for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                Map<String, Object> body = Map.of(
                        "model", model,
                        "input", texts
                );

                EmbeddingResponse response = webClient.post()
                        .uri("/embeddings")
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(EmbeddingResponse.class)
                        .block();

                return response;

            } catch (WebClientResponseException.TooManyRequests e) {
                long delay = INITIAL_RETRY_DELAY_MS * (long) Math.pow(2, attempt);
                log.warn("OpenAI rate limit (429). Retry {}/{} in {} ms",
                        attempt + 1, MAX_RETRY_ATTEMPTS, delay);
                sleep(delay);

            } catch (WebClientResponseException e) {
                throw new AIServiceException(
                        "OpenAI embedding failed: " + e.getResponseBodyAsString(), e);
            }
        }

        throw new AIServiceException(
                "OpenAI rate limit exceeded after " + MAX_RETRY_ATTEMPTS + " retries");
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AIServiceException("Embedding retry interrupted", e);
        }
    }

    public String toPgVectorString(List<Double> vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(vector.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}