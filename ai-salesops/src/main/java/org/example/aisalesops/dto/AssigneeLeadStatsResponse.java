package org.example.aisalesops.dto;

public class AssigneeLeadStatsResponse {

    private Long userId;
    private String fullName;
    private long total;
    private long hot;
    private long newLeads;
    private long openTasks;
    private long overdueTasks;

    public AssigneeLeadStatsResponse(Long userId,
                                     String fullName,
                                     long total,
                                     long hot,
                                     long newLeads,
                                     long openTasks,
                                     long overdueTasks) {
        this.userId = userId;
        this.fullName = fullName;
        this.total = total;
        this.hot = hot;
        this.newLeads = newLeads;
        this.openTasks = openTasks;
        this.overdueTasks = overdueTasks;
    }

    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public long getTotal() { return total; }
    public long getHot() { return hot; }
    public long getNewLeads() { return newLeads; }
    public long getOpenTasks() { return openTasks; }
    public long getOverdueTasks() { return overdueTasks; }
}