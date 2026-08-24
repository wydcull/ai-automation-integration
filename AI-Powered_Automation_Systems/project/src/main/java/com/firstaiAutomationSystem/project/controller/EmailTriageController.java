package com.firstaiAutomationSystem.project.controller;

import com.firstaiAutomationSystem.project.model.EmailTriageRequest;
import com.firstaiAutomationSystem.project.model.EmailTriageResponse;
import com.firstaiAutomationSystem.project.model.dto.EmailTriageListItem;
import com.firstaiAutomationSystem.project.service.EmailTriageService;
import com.firstaiAutomationSystem.project.validator.EmailValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/automation/email-triage")
public class EmailTriageController {

    private static final Logger log = LoggerFactory.getLogger(EmailTriageController.class);
    private final EmailTriageService emailTriageService;
    private EmailValidator emailValidator;

    public EmailTriageController(EmailTriageService emailTriageService) {
        this.emailTriageService = emailTriageService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public EmailTriageResponse triage(
            @RequestParam("senderEmail")
            @NotBlank @Email @Size(max = 255) String senderEmail,

            @RequestParam("subject")
            @NotBlank @Size(min = 1, max = 500) String subject,

            @RequestParam("body")
            @NotBlank @Size(min = 10, max = 10000) String body,

            @RequestParam(value = "document", required = false) MultipartFile document) {

        // Validate file
        if (document != null && !document.isEmpty()) {
            emailValidator.validateDocument(document);
        }

        EmailTriageRequest request = new EmailTriageRequest(senderEmail, subject, body);
        return emailTriageService.process(request, document);
    }

    @GetMapping
    public Page<EmailTriageListItem> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority,
            @PageableDefault(size = 20, sort = "processedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return emailTriageService.search(category, priority, pageable);
    }
    @GetMapping("/{id}")
    public EmailTriageResponse findById(@PathVariable Long id) {
        return emailTriageService.findById(id);
    }
}