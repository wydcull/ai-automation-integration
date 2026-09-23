package org.example.aisalesops.dto;

import java.time.OffsetDateTime;

public class HotLeadResponse {

    private Long id;
    private String leadCode;
    private String companyName;
    private String productName;
    private Integer score;
    private String scoreBand;
    private String status;
    private String assignedUserName;
    private OffsetDateTime createdAt;

    public HotLeadResponse(Long id,
                           String leadCode,
                           String companyName,
                           String productName,
                           Integer score,
                           String scoreBand,
                           String status,
                           String assignedUserName,
                           OffsetDateTime createdAt) {
        this.id = id;
        this.leadCode = leadCode;
        this.companyName = companyName;
        this.productName = productName;
        this.score = score;
        this.scoreBand = scoreBand;
        this.status = status;
        this.assignedUserName = assignedUserName;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getLeadCode() { return leadCode; }
    public String getCompanyName() { return companyName; }
    public String getProductName() { return productName; }
    public Integer getScore() { return score; }
    public String getScoreBand() { return scoreBand; }
    public String getStatus() { return status; }
    public String getAssignedUserName() { return assignedUserName; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}