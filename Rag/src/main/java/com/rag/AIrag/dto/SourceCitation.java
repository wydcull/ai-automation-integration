package com.rag.AIrag.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SourceCitation {
    private UUID documentId;
    private String filename;
    private int chunkIndex;
    private double score;
}