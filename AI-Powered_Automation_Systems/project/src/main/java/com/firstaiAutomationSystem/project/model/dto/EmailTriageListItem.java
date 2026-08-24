package com.firstaiAutomationSystem.project.model.dto;

import java.time.LocalDateTime;
public record EmailTriageListItem(
        Long id,
        String senderEmail,
        String subject,
        String category,
        String priority,
        String summary,
        LocalDateTime processedAt,
        Boolean approved,
        Boolean rejected,
        Boolean replySent
) {
}