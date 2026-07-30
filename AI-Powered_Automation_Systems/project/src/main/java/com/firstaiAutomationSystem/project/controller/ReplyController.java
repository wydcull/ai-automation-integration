package com.firstaiAutomationSystem.project.controller;

import com.firstaiAutomationSystem.project.model.EmailTriageRecord;
import com.firstaiAutomationSystem.project.service.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {

    private final ReplyService replyService;
    private static final Logger log = LoggerFactory.getLogger(ReplyController.class);

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    /**
     * Approve a reply (ADMIN and APPROVER only)
     */
    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
    public ResponseEntity<?> approveReply(
            @PathVariable Long id,
            @RequestParam(required = false) String approvedBy) {

        // Get current user if approvedBy not provided
        if (approvedBy == null) {
            approvedBy = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
        }

        EmailTriageRecord record = replyService.approveReply(id, approvedBy);
        log.info("Approve request: emailId={}, user={}", id, approvedBy);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Reply approved",
                "recordId", record.getId(),
                "approvedBy", record.getApprovedBy(),
                "approvedAt", record.getApprovedAt()
        ));
    }

    /**
     * Reject a reply (ADMIN and APPROVER only)
     */
    @PostMapping("/reject/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
    public ResponseEntity<?> rejectReply(
            @PathVariable Long id,
            @RequestParam(required = false) String rejectedBy,
            @RequestParam(required = false) String reason) {

        if (rejectedBy == null) {
            rejectedBy = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
        }

        EmailTriageRecord record = replyService.rejectReply(id, rejectedBy, reason);
        log.info("Reject request: emailId={}, user={}", id, rejectedBy);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Reply rejected",
                "recordId", record.getId()
        ));
    }

    /**
     * Send approved reply (ADMIN and APPROVER only)
     */
    @PostMapping("/send/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
    public ResponseEntity<?> sendReply(@PathVariable Long id) {
        try {
            EmailTriageRecord record = replyService.sendReply(id);
            log.info("Send reply request: emailId={}", id);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Reply sent successfully",
                    "gmailMessageId", record.getReplyMessageId(),
                    "sentAt", record.getReplyAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get pending approvals (All authenticated users)
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingApprovals() {
        List<EmailTriageRecord> pending = replyService.getPendingApprovals();
        return ResponseEntity.ok(pending);
    }

    /**
     * Get approved but not sent (All authenticated users)
     */
    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedNotSent() {
        List<EmailTriageRecord> approved = replyService.getApprovedNotSent();
        return ResponseEntity.ok(approved);
    }

    /**
     * Edit draft reply (ADMIN and APPROVER only)
     */
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
    public ResponseEntity<?> editDraftReply(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String newDraftReply = body.get("draftReply");
        if (newDraftReply == null || newDraftReply.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "draftReply cannot be empty"
            ));
        }

        EmailTriageRecord record = replyService.editDraftReply(id, newDraftReply);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Draft reply updated",
                "recordId", record.getId()
        ));
    }

    /**
     * Approve and send in one step (ADMIN and APPROVER only)
     */
    @PostMapping("/approve-and-send/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
    public ResponseEntity<?> approveAndSend(
            @PathVariable Long id,
            @RequestParam(required = false) String approvedBy) {

        try {
            if (approvedBy == null) {
                approvedBy = SecurityContextHolder.getContext()
                        .getAuthentication().getName();
            }

            // Approve first
            replyService.approveReply(id, approvedBy);

            // Then send
            EmailTriageRecord record = replyService.sendReply(id);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Reply approved and sent",
                    "gmailMessageId", record.getReplyMessageId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}