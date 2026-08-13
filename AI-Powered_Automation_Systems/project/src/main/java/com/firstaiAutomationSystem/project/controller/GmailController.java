package com.firstaiAutomationSystem.project.controller;

import com.firstaiAutomationSystem.project.service.GmailAuthService;
import com.firstaiAutomationSystem.project.service.GmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gmail")
public class GmailController {

    private final GmailService gmailService;
    private final GmailAuthService gmailAuthService;
    private static final Logger log = LoggerFactory.getLogger(GmailController.class);

    @Value("${gmail.frontend.url}")
    private String frontendUrl;

    public GmailController(GmailService gmailService, GmailAuthService gmailAuthService) {
        this.gmailService = gmailService;
        this.gmailAuthService = gmailAuthService;
    }

    @GetMapping("/connect")
    public ResponseEntity<Map<String, String>> connect() {
        try {
            String authUrl = gmailAuthService.createAuthorizationUrl();
            return ResponseEntity.ok(Map.of("authUrl", authUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {

        String redirect;
        try {
            if (error != null) {
                redirect = frontendUrl + "/gmail?error=" + error;
            } else if (code == null || state == null || !gmailAuthService.isValidState(state)) {
                redirect = frontendUrl + "/gmail?error=invalid_state";
            } else {
                gmailAuthService.handleCallback(code);
                redirect = frontendUrl + "/gmail?connected=1";
            }
        } catch (Exception e) {
            log.error("Gmail callback failed", e);
            redirect = frontendUrl + "/gmail?error=callback_failed";
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirect))
                .build();
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        if (!gmailAuthService.isConnected()) {
            return ResponseEntity.ok(Map.of(
                    "status", "disconnected",
                    "message", "Gmail is not connected"
            ));
        }
        String email = gmailAuthService.getConnectedEmail();
        return ResponseEntity.ok(Map.of(
                "status", "connected",
                "message", "Gmail connection is active",
                "email", email != null ? email : ""
        ));
    }

    @PostMapping("/fetch")
    public ResponseEntity<Map<String, Object>> fetchEmails() {
        try {
            List<String> processedIds = gmailService.fetchAndProcessUnreadEmails();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "processed", processedIds.size(),
                    "messageIds", processedIds
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<Map<String, String>> revokeAccess() {
        try {
            gmailAuthService.invalidateToken();
            return ResponseEntity.ok(Map.of("status", "success", "message", "Gmail access revoked"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}