package org.example.aisalesops.dto;

import java.time.OffsetDateTime;

public class SlaBreachResponse {

    private Long leadId;
    private String leadCode;
    private String companyName;
    private String scoreBand;
    private String assignedUserName;
    private OffsetDateTime dueAt;
    private long hoursOverdue;

    public SlaBreachResponse(Long leadId,
                             String leadCode,
                             String companyName,
                             String scoreBand,
                             String assignedUserName,
                             OffsetDateTime dueAt,
                             long hoursOverdue) {
        this.leadId = leadId;
        this.leadCode = leadCode;
        this.companyName = companyName;
        this.scoreBand = scoreBand;
        this.assignedUserName = assignedUserName;
        this.dueAt = dueAt;
        this.hoursOverdue = hoursOverdue;
    }

    public Long getLeadId() { return leadId; }
    public String getLeadCode() { return leadCode; }
    public String getCompanyName() { return companyName; }
    public String getScoreBand() { return scoreBand; }
    public String getAssignedUserName() { return assignedUserName; }
    public OffsetDateTime getDueAt() { return dueAt; }
    public long getHoursOverdue() { return hoursOverdue; }
}