package com.firstaiAutomationSystem.project.model;

import java.util.Map;

public record AiTriageResult(
        String category,
        String priority,
        String summary,
        String draftReply,
        Map<String, String> extractedData
) {
}