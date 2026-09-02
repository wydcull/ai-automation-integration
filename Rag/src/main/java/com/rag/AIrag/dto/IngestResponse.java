package com.rag.AIrag.dto;

import com.rag.AIrag.model.DocumentStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class IngestResponse {
    private UUID documentId;
    private String filename;
    private int chunkCount;
    private DocumentStatus status;
}