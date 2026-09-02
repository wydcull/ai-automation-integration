package com.rag.AIrag.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AskResponse {
    private String answer;
    private List<SourceCitation> sources;
}