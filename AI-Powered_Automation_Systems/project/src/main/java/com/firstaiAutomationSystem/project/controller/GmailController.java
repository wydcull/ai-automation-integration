package com.firstaiAutomationSystem.project.controller;

import com.firstaiAutomationSystem.project.service.GmailAuthService;
import com.firstaiAutomationSystem.project.service.GmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gmail")
public class GmailController {

    private final GmailService gmailService;
    private final GmailAuthService gmailAuthService;
    private static final Logger log = LoggerFactory.getLogger(GmailController.class);

    public GmailController(GmailService gmailService, GmailAuthService gmailAuthService) {
        this.gmailService = gmailService;
        this.gmailAuthService = gmailAuthService;
    }

    @GetMapping("/authorize")
    public ResponseEntity<Map<String, String>> authorize() {
        try {
            gmailAuthService.getGmailService();
            log.info("Gmail authorize requested by user");

            return ResponseEntity.ok(Map.of("status", "authorized", "message", "Gmail authorization successful"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/fetch")
    public ResponseEntity<Map<String, Object>> fetchEmails() {
        try {
            List<String> processedIds = gmailService.fetchAndProcessUnreadEmails();
            log.info("Gmail fetch requested");

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "processed", processedIds.size(),
                    "messageIds", processedIds
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        try {
            gmailAuthService.getGmailService();
            return ResponseEntity.ok(Map.of("status", "connected", "message", "Gmail connection is active"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("status", "disconnected", "message", e.getMessage()));
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<Map<String, String>> revokeAccess() {
        try {
            gmailAuthService.invalidateToken();
            log.info("Gmail access revoked");
            return ResponseEntity.ok(Map.of("status", "success", "message", "Gmail access revoked"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}