package org.example.aisalesops.controller;

import org.example.aisalesops.entity.EmailMessage;
import org.example.aisalesops.service.EmailMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email-messages")
public class EmailMessageController {

    private final EmailMessageService emailMessageService;


    public EmailMessageController(
            EmailMessageService emailMessageService
    ) {
        this.emailMessageService = emailMessageService;
    }


    // =========================================
    // CREATE EMAIL MESSAGE
    // POST: /api/email-messages
    // =========================================

    @PostMapping
    public EmailMessage createEmailMessage(
            @RequestBody EmailMessage emailMessage
    ) {

        return emailMessageService
                .createEmailMessage(emailMessage);
    }


    // =========================================
    // GET ALL EMAIL MESSAGES
    // GET: /api/email-messages
    // =========================================

    @GetMapping
    public List<EmailMessage> getAllEmailMessages() {

        return emailMessageService
                .getAllEmailMessages();
    }


    // =========================================
    // GET EMAIL MESSAGE BY ID
    // GET: /api/email-messages/{id}
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<EmailMessage> getEmailMessageById(
            @PathVariable Long id
    ) {

        return emailMessageService
                .getEmailMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================
    // FULL UPDATE
    // PUT: /api/email-messages/{id}
    // =========================================

    @PutMapping("/{id}")
    public EmailMessage updateEmailMessage(
            @PathVariable Long id,
            @RequestBody EmailMessage emailMessage
    ) {

        return emailMessageService
                .updateEmailMessage(
                        id,
                        emailMessage
                );
    }


    // =========================================
    // PARTIAL UPDATE
    // PATCH: /api/email-messages/{id}
    // =========================================

    @PatchMapping("/{id}")
    public EmailMessage partialUpdateEmailMessage(
            @PathVariable Long id,
            @RequestBody EmailMessage emailMessage
    ) {

        return emailMessageService
                .partialUpdateEmailMessage(
                        id,
                        emailMessage
                );
    }


    // =========================================
    // DELETE EMAIL MESSAGE
    // DELETE: /api/email-messages/{id}
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmailMessage(
            @PathVariable Long id
    ) {

        emailMessageService.deleteEmailMessage(id);

        return ResponseEntity.ok(
                "Email Message deleted successfully"
        );
    }
}