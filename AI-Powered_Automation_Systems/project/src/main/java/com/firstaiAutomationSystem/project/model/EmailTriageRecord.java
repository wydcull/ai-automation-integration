package com.firstaiAutomationSystem.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "email_triage_records")
public class EmailTriageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderEmail;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    private String category;
    private String priority;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String draftReply;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> extractedData;

    private LocalDateTime processedAt;

    @Column(columnDefinition = "TEXT")
    private String documentFileName;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> documentExtractedData;

    private String gmailMessageId;        // Gmail message ID
    private String gmailThreadId;       // Gmail thread ID
    private Boolean processed;            // Processed by scheduler?
    private Boolean replySent = false;            // Was reply sent?
    private LocalDateTime replyAt;        // When reply was sent
    private String replyMessageId;      // Gmail ID of sent reply

    public String getGmailMessageId() {
        return gmailMessageId;
    }
    public String getGmailThreadId() {
        return gmailThreadId;
    }

    public void setGmailThreadId(String gmailThreadId) {
        this.gmailThreadId = gmailThreadId;
    }

    public void setGmailMessageId(String gmailMessageId) {
        this.gmailMessageId = gmailMessageId;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public Boolean getReplySent() {
        return replySent;
    }

    public void setReplySent(Boolean replySent) {
        this.replySent = replySent;
    }

    public LocalDateTime getReplyAt() {
        return replyAt;
    }

    public void setReplyAt(LocalDateTime replyAt) {
        this.replyAt = replyAt;
    }

    public String getReplyMessageId() {
        return replyMessageId;
    }

    public void setReplyMessageId(String replyMessageId) {
        this.replyMessageId = replyMessageId;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDraftReply() {
        return draftReply;
    }

    public void setDraftReply(String draftReply) {
        this.draftReply = draftReply;
    }

    public Map<String, String> getExtractedData() {
        return extractedData;
    }

    public void setExtractedData(Map<String, String> extractedData) {
        this.extractedData = extractedData;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getDocumentFileName() {
        return documentFileName;
    }
    public void setDocumentFileName(String documentFileName) {
        this.documentFileName = documentFileName;
    }
    public Map<String, Object> getDocumentExtractedData() {
        return documentExtractedData;
    }
    public void setDocumentExtractedData(Map<String, Object> documentExtractedData) {
        this.documentExtractedData = documentExtractedData;
    }
}