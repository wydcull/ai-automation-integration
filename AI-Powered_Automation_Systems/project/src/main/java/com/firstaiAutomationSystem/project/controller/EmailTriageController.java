package com.firstaiAutomationSystem.project.controller;

import com.firstaiAutomationSystem.project.model.EmailTriageRequest;
import com.firstaiAutomationSystem.project.model.EmailTriageResponse;
import com.firstaiAutomationSystem.project.service.EmailTriageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/automation/email-triage")
public class EmailTriageController {

    private static final Logger log = LoggerFactory.getLogger(EmailTriageController.class);
    private final EmailTriageService emailTriageService;

    public EmailTriageController(EmailTriageService emailTriageService) {
        this.emailTriageService = emailTriageService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public EmailTriageResponse triage(
            @RequestParam("senderEmail") String senderEmail,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body,
            @RequestParam(value = "document", required = false) MultipartFile document) {

        EmailTriageRequest request = new EmailTriageRequest(senderEmail, subject, body);
        return emailTriageService.process(request, document);
    }

    @GetMapping
    public List<EmailTriageResponse> findAll() {
        return emailTriageService.findAll();
    }

    @GetMapping("/{id}")
    public EmailTriageResponse findById(@PathVariable Long id) {
        return emailTriageService.findById(id);
    }
}