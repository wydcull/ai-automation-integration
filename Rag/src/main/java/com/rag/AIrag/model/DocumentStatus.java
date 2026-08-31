package com.rag.AIrag.model;

public enum DocumentStatus {
    PENDING,   // uploaded, not yet indexed
    INDEXED,   // chunks + embeddings saved
    FAILED     // parsing/embedding failed
}