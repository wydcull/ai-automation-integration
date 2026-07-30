package com.firstaiAutomationSystem.project.scheduler;

import com.firstaiAutomationSystem.project.service.GmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "gmail.scheduler.enabled", havingValue = "true", matchIfMissing = false)
public class GmailScheduler {

    private final GmailService gmailService;

    private static final Logger log = LoggerFactory.getLogger(GmailScheduler.class);

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
        log.info("Gmail scheduler started");

        try {
            System.out.println("Gmail Scheduler: Fetching unread emails...");
            List<String> processedIds = gmailService.fetchAndProcessUnreadEmails();
            log.info("Gmail scheduler completed: processedCount={}", processedIds.size()+ " emails");
        } catch (Exception e) {
            log.error("Gmail scheduler failed", e);
        }
    }
}