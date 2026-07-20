package com.firstaiAutomationSystem.project.scheduler;

import com.firstaiAutomationSystem.project.service.GmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "gmail.scheduler.enabled", havingValue = "true", matchIfMissing = false)
public class GmailScheduler {

    private final GmailService gmailService;

    @Value("${gmail.scheduler.enabled}")
    private boolean enabled;

    public GmailScheduler(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @Scheduled(cron = "${gmail.scheduler.cron}")
    public void fetchAndProcessEmails() {
        if (!enabled) {
            return;
        }

        try {
            System.out.println("Gmail Scheduler: Fetching unread emails...");
            List<String> processedIds = gmailService.fetchAndProcessUnreadEmails();
            System.out.println("Gmail Scheduler: Processed " + processedIds.size() + " emails");
        } catch (Exception e) {
            System.err.println("Gmail Scheduler Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}