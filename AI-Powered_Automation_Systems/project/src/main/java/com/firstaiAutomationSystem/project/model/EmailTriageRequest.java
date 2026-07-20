package com.firstaiAutomationSystem.project.model;

public record EmailTriageRequest(
        String senderEmail,
        String subject,
        String body
) {}

