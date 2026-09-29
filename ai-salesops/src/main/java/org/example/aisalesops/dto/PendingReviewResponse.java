package org.example.aisalesops.dto;

public class PendingReviewResponse {

    private Long leadId;
    private String leadCode;
    private String companyName;
    private String fieldName;
    private java.math.BigDecimal confidence;
    private String aiValue;

    public PendingReviewResponse(Long leadId, String leadCode, String companyName,
                                 String fieldName, java.math.BigDecimal confidence,
                                 String aiValue) {
        this.leadId = leadId;
        this.leadCode = leadCode;
        this.companyName = companyName;
        this.fieldName = fieldName;
        this.confidence = confidence;
        this.aiValue = aiValue;
    }

    public Long getLeadId() { return leadId; }
    public String getLeadCode() { return leadCode; }
    public String getCompanyName() { return companyName; }
    public String getFieldName() { return fieldName; }
    public java.math.BigDecimal getConfidence() { return confidence; }
    public String getAiValue() { return aiValue; }
}