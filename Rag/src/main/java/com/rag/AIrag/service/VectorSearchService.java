package com.rag.AIrag.service;

import com.rag.AIrag.dto.RetrievedChunk;
import com.rag.AIrag.repository.DocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VectorSearchService {

    private final DocumentChunkRepository chunkRepository;

    @Value("${rag.top-k}")
    private int defaultTopK;

    @Value("${rag.min-similarity-score}")
    private double minScore;

    public List<RetrievedChunk> search(String pgVector, int topK, UUID documentId) {
        String docFilter = documentId == null ? null : documentId.toString();

        return chunkRepository.findSimilar(pgVector, topK, docFilter).stream()
                .map(row -> new RetrievedChunk(
                        UUID.fromString(row[0].toString()),
                        UUID.fromString(row[1].toString()),
                        ((Number) row[2]).intValue(),
                        row[3].toString(),
                        ((Number) row[4]).doubleValue()
                ))
                .filter(c -> c.score() >= minScore)
                .toList();
    }
}
