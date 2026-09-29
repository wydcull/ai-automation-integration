package org.example.aisalesops.dto;

import java.time.OffsetDateTime;

public class DashboardTaskResponse {

    private Long id;
    private String title;
    private String priority;
    private String status;
    private OffsetDateTime dueAt;
    private boolean overdue;
    private Long leadId;
    private String leadCode;
    private String companyName;
    private Long assignedUserId;
    private String assignedUserName;

    public DashboardTaskResponse(Long id, String title, String priority, String status,
                                 OffsetDateTime dueAt, boolean overdue,
                                 Long leadId, String leadCode, String companyName,
                                 Long assignedUserId, String assignedUserName) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.dueAt = dueAt;
        this.overdue = overdue;
        this.leadId = leadId;
        this.leadCode = leadCode;
        this.companyName = companyName;
        this.assignedUserId = assignedUserId;
        this.assignedUserName = assignedUserName;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public OffsetDateTime getDueAt() { return dueAt; }
    public boolean isOverdue() { return overdue; }
    public Long getLeadId() { return leadId; }
    public String getLeadCode() { return leadCode; }
    public String getCompanyName() { return companyName; }
    public Long getAssignedUserId() { return assignedUserId; }
    public String getAssignedUserName() { return assignedUserName; }
}