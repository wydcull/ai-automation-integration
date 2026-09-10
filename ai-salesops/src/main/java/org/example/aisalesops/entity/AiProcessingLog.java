package org.example.aisalesops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_processing_logs")
public class AiProcessingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "request_id",
            nullable = false,
            unique = true
    )
    private UUID requestId;


    @Column(name = "lead_id")
    private Long leadId;


    @Column(name = "email_message_id")
    private Long emailMessageId;


    @Column(
            nullable = false,
            length = 64
    )
    private String operation;


    @Column(
            name = "model_name",
            length = 64
    )
    private String modelName;


    @Column(
            name = "input_ref",
            columnDefinition = "TEXT"
    )
    private String inputRef;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "output_json",
            columnDefinition = "jsonb"
    )
    private String outputJson;


    @Column(
            precision = 5,
            scale = 2
    )
    private BigDecimal confidence;


    @Column(
            nullable = false,
            length = 16
    )
    private String status;


    @Column(
            name = "error_message",
            columnDefinition = "TEXT"
    )
    private String errorMessage;


    @Column(name = "processing_ms")
    private Integer processingMs;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;


    // =========================================
    // DEFAULT CONSTRUCTOR
    // =========================================

    public AiProcessingLog() {
    }


    // =========================================
    // AUTO SET CREATED AT
    // =========================================

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {

            createdAt = OffsetDateTime.now();
        }
    }


    // =========================================
    // GETTERS AND SETTERS
    // =========================================

    public Long getId() {

        return id;
    }


    public void setId(Long id) {

        this.id = id;
    }


    public UUID getRequestId() {

        return requestId;
    }


    public void setRequestId(UUID requestId) {

        this.requestId = requestId;
    }


    public Long getLeadId() {

        return leadId;
    }


    public void setLeadId(Long leadId) {

        this.leadId = leadId;
    }


    public Long getEmailMessageId() {

        return emailMessageId;
    }


    public void setEmailMessageId(Long emailMessageId) {

        this.emailMessageId = emailMessageId;
    }


    public String getOperation() {

        return operation;
    }


    public void setOperation(String operation) {

        this.operation = operation;
    }


    public String getModelName() {

        return modelName;
    }


    public void setModelName(String modelName) {

        this.modelName = modelName;
    }


    public String getInputRef() {

        return inputRef;
    }


    public void setInputRef(String inputRef) {

        this.inputRef = inputRef;
    }


    public String getOutputJson() {

        return outputJson;
    }


    public void setOutputJson(String outputJson) {

        this.outputJson = outputJson;
    }


    public BigDecimal getConfidence() {

        return confidence;
    }


    public void setConfidence(BigDecimal confidence) {

        this.confidence = confidence;
    }


    public String getStatus() {

        return status;
    }


    public void setStatus(String status) {

        this.status = status;
    }


    public String getErrorMessage() {

        return errorMessage;
    }


    public void setErrorMessage(String errorMessage) {

        this.errorMessage = errorMessage;
    }


    public Integer getProcessingMs() {

        return processingMs;
    }


    public void setProcessingMs(Integer processingMs) {

        this.processingMs = processingMs;
    }


    public OffsetDateTime getCreatedAt() {

        return createdAt;
    }


    public void setCreatedAt(OffsetDateTime createdAt) {

        this.createdAt = createdAt;
    }
}