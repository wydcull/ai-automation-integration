package com.rag.AIrag.dto;

import java.util.UUID;
public record RetrievedChunk(
        UUID chunkId,
        UUID documentId,
        int chunkIndex,
        String content,
        double score
) {}
