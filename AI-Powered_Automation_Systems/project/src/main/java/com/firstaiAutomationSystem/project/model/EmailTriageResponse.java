package com.firstaiAutomationSystem.project.model;

import java.time.LocalDateTime;
import java.util.Map;
public record EmailTriageResponse(
        Long id,
        String senderEmail,
        String subject,
        String body,                    // NEW
        String category,
        String priority,
        String summary,
        String draftReply,
        Map<String, String> extractedData,
        String documentFileName,
        Map<String, Object> documentExtractedData,
        LocalDateTime processedAt,
        Boolean approved,               // NEW
        Boolean rejected,               // NEW
        Boolean replySent,              // NEW
        String approvedBy,              // NEW
        LocalDateTime approvedAt,       // NEW
        String rejectionReason          // NEW
) {
}