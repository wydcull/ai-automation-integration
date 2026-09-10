package org.example.aisalesops.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "email_messages")
public class EmailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "external_message_id",
            nullable = false,
            unique = true,
            length = 255
    )
    private String externalMessageId;


    @Column(nullable = false, length = 255)
    private String sender;


    @Column(length = 255)
    private String recipient;


    @Column(columnDefinition = "TEXT")
    private String subject;


    @Column(name = "body_text", columnDefinition = "TEXT")
    private String bodyText;


    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;


    @Column(
            name = "process_status",
            nullable = false,
            length = 32
    )
    private String processStatus = "RECEIVED";


    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;


    @Column(
            name = "retry_count",
            nullable = false
    )
    private Integer retryCount = 0;


    @Column(name = "processed_at")
    private OffsetDateTime processedAt;


    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;


    // Default Constructor
    public EmailMessage() {
    }


    // Automatically set createdAt before saving
    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }

        if (processStatus == null) {
            processStatus = "RECEIVED";
        }

        if (retryCount == null) {
            retryCount = 0;
        }
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getExternalMessageId() {
        return externalMessageId;
    }


    public void setExternalMessageId(
            String externalMessageId
    ) {
        this.externalMessageId = externalMessageId;
    }


    public String getSender() {
        return sender;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }


    public String getRecipient() {
        return recipient;
    }


    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }


    public String getSubject() {
        return subject;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getBodyText() {
        return bodyText;
    }


    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }


    public OffsetDateTime getReceivedAt() {
        return receivedAt;
    }


    public void setReceivedAt(
            OffsetDateTime receivedAt
    ) {
        this.receivedAt = receivedAt;
    }


    public String getProcessStatus() {
        return processStatus;
    }


    public void setProcessStatus(
            String processStatus
    ) {
        this.processStatus = processStatus;
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorMessage(
            String errorMessage
    ) {
        this.errorMessage = errorMessage;
    }


    public Integer getRetryCount() {
        return retryCount;
    }


    public void setRetryCount(
            Integer retryCount
    ) {
        this.retryCount = retryCount;
    }


    public OffsetDateTime getProcessedAt() {
        return processedAt;
    }


    public void setProcessedAt(
            OffsetDateTime processedAt
    ) {
        this.processedAt = processedAt;
    }


    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(
            OffsetDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }
}