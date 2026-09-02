package com.rag.AIrag.dto;

import com.rag.AIrag.model.DocumentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class DocumentSummary {
    private UUID id;
    private String filename;
    private DocumentStatus status;
    private Instant uploadedAt;
    private long chunkCount;
}