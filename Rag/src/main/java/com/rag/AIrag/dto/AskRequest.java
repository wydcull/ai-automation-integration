package com.rag.AIrag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class AskRequest {
    @NotBlank
    private String question;
    private Integer topK;
    private UUID documentId;
}