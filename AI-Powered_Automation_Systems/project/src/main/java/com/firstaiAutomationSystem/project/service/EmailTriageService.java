package com.firstaiAutomationSystem.project.service;

import com.firstaiAutomationSystem.project.exception.AiServiceException;
import com.firstaiAutomationSystem.project.exception.DocumentProcessingException;
import com.firstaiAutomationSystem.project.model.*;
import com.firstaiAutomationSystem.project.repository.EmailTriageRepository;
import com.firstaiAutomationSystem.project.util.InputSanitizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailTriageService {

    private final InputSanitizer sanitizer;
    private final OpenAiTriageService openAiTriageService;
    private final EmailTriageRepository repository;
    private final DocumentExtractionService documentExtractionService;
    private final AiDocumentExtractionService aiDocumentExtractionService;
    private static final Logger log = LoggerFactory.getLogger(EmailTriageService.class);


    // Update constructor to inject new services:
    public EmailTriageService(InputSanitizer sanitizer,OpenAiTriageService openAiTriageService,
                              EmailTriageRepository repository,
                              DocumentExtractionService documentExtractionService,
                              AiDocumentExtractionService aiDocumentExtractionService) {
        this.sanitizer=sanitizer;
        this.openAiTriageService = openAiTriageService;
        this.repository = repository;
        this.documentExtractionService = documentExtractionService;
        this.aiDocumentExtractionService = aiDocumentExtractionService;
    }
    @Transactional(rollbackFor = {
            AiServiceException.class,
            DocumentProcessingException.class,
            Exception.class
    })
    public EmailTriageResponse process(EmailTriageRequest request, MultipartFile document) {

        String sanitizedEmail = sanitizer.sanitizeEmail(request.senderEmail());
        String sanitizedSubject = sanitizer.sanitizeHtml(request.subject());
        String sanitizedBody = sanitizer.sanitizeForDatabase(request.body());
        log.info("Email triage started: sender={}, subject={}", sanitizedEmail, sanitizedSubject);

        // 1. Extract document data FIRST (if provided)
        String documentFileName = null;
        Map<String, Object> documentData = null;
        String candidateName = null;

        if (document != null && !document.isEmpty()) {
            documentFileName = document.getOriginalFilename();
            String rawText = documentExtractionService.extractTextFromPdf(document);
            if (rawText != null && !rawText.isBlank()) {
                DocumentExtractionResult extractionResult =
                        aiDocumentExtractionService.extractStructuredData(rawText);
                if (extractionResult != null) {
                    documentData = Map.of(
                            "documentType", extractionResult.documentType() != null ? extractionResult.documentType() : "UNKNOWN",
                            "summary", extractionResult.summary() != null ? extractionResult.summary() : "",
                            "extractedFields", extractionResult.extractedFields() != null ? extractionResult.extractedFields() : Map.of()
                    );

                    // Extract candidate name if it's a resume
                    if ("RESUME".equals(extractionResult.documentType())
                            && extractionResult.extractedFields() != null) {
                        candidateName = (String) extractionResult.extractedFields().get("candidateName");
                    }
                }
            }
        }
        log.debug("Document extracted: fileName={}", documentFileName);

        // 2. Triage email text WITH candidate name context
        AiTriageResult aiResult = openAiTriageService.triage(
                sanitizedSubject,
                sanitizedBody,
                candidateName  // Pass the name
        );

        // Rest of the method stays the same...
        EmailTriageRecord record = new EmailTriageRecord();
        record.setSenderEmail(sanitizedEmail );
        record.setSubject(sanitizedSubject );
        record.setBody(sanitizedBody );
        record.setCategory(aiResult.category());
        record.setPriority(aiResult.priority());
        record.setSummary(aiResult.summary());
        record.setDraftReply(aiResult.draftReply());
        record.setExtractedData(aiResult.extractedData());
        record.setDocumentFileName(documentFileName);
        record.setDocumentExtractedData(documentData);
        record.setProcessedAt(LocalDateTime.now());

        EmailTriageRecord saved = repository.save(record);
        log.info("Email triaged: id={}, category={}, priority={}",
                saved.getId(), saved.getCategory(), saved.getPriority());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EmailTriageResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmailTriageResponse findById(Long id) {
        EmailTriageRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Triage record not found: " + id));
        return toResponse(record);
    }

    // Update toResponse method:
    private EmailTriageResponse toResponse(EmailTriageRecord record) {
        return new EmailTriageResponse(
                record.getId(),
                record.getSenderEmail(),
                record.getSubject(),
                record.getCategory(),
                record.getPriority(),
                record.getSummary(),
                record.getDraftReply(),
                record.getExtractedData(),
                record.getDocumentFileName(),
                record.getDocumentExtractedData(),
                record.getProcessedAt()
        );
    }

    @Transactional
    public EmailTriageResponse processFromGmail(EmailTriageRequest request, MultipartFile document,
                                                String gmailMessageId, String gmailThreadId) {
        // 1. Extract document data FIRST (if provided)
        String documentFileName = null;
        Map<String, Object> documentData = null;
        String candidateName = null;

        if (document != null && !document.isEmpty()) {
            documentFileName = document.getOriginalFilename();
            String rawText = documentExtractionService.extractTextFromPdf(document);
            if (rawText != null && !rawText.isBlank()) {
                DocumentExtractionResult extractionResult =
                        aiDocumentExtractionService.extractStructuredData(rawText);
                if (extractionResult != null) {
                    documentData = Map.of(
                            "documentType", extractionResult.documentType() != null ? extractionResult.documentType() : "UNKNOWN",
                            "summary", extractionResult.summary() != null ? extractionResult.summary() : "",
                            "extractedFields", extractionResult.extractedFields() != null ? extractionResult.extractedFields() : Map.of()
                    );

                    // Extract candidate name if it's a resume
                    if ("RESUME".equals(extractionResult.documentType())
                            && extractionResult.extractedFields() != null) {
                        candidateName = (String) extractionResult.extractedFields().get("candidateName");
                    }
                }
            }
        }

        // 2. Triage email text WITH candidate name context
        AiTriageResult aiResult = openAiTriageService.triage(
                request.subject(),
                request.body(),
                candidateName
        );

        // 3. Save to database with Gmail IDs
        EmailTriageRecord record = new EmailTriageRecord();
        record.setSenderEmail(request.senderEmail());
        record.setSubject(request.subject());
        record.setBody(request.body());
        record.setCategory(aiResult.category());
        record.setPriority(aiResult.priority());
        record.setSummary(aiResult.summary());
        record.setDraftReply(aiResult.draftReply());
        record.setExtractedData(aiResult.extractedData());
        record.setDocumentFileName(documentFileName);
        record.setDocumentExtractedData(documentData);
        record.setGmailMessageId(gmailMessageId);
        record.setGmailThreadId(gmailThreadId);
        record.setProcessed(true);
        record.setReplySent(false);
        record.setProcessedAt(LocalDateTime.now());

        EmailTriageRecord saved = repository.save(record);
        return toResponse(saved);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void markReplySent(Long id) {
        EmailTriageRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Triage record not found: " + id));
        record.setReplySent(true);
        record.setReplyAt(LocalDateTime.now());
        repository.save(record);
    }
}