package org.example.aisalesops.dto;

public class SalesSummaryResponse {

    private long totalLeads;
    private long newLeads;
    private long hotLeads;
    private long qualifiedLeads;
    private long openFollowUps;
    private long overdueFollowUps;
    private long pendingReview;
    private long unassignedLeads;

    public SalesSummaryResponse(long totalLeads, long newLeads, long hotLeads,
                                long qualifiedLeads, long openFollowUps,
                                long overdueFollowUps, long pendingReview,
                                long unassignedLeads) {
        this.totalLeads = totalLeads;
        this.newLeads = newLeads;
        this.hotLeads = hotLeads;
        this.qualifiedLeads = qualifiedLeads;
        this.openFollowUps = openFollowUps;
        this.overdueFollowUps = overdueFollowUps;
        this.pendingReview = pendingReview;
        this.unassignedLeads = unassignedLeads;
    }

    public long getTotalLeads() { return totalLeads; }
    public long getNewLeads() { return newLeads; }
    public long getHotLeads() { return hotLeads; }
    public long getQualifiedLeads() { return qualifiedLeads; }
    public long getOpenFollowUps() { return openFollowUps; }
    public long getOverdueFollowUps() { return overdueFollowUps; }
    public long getPendingReview() { return pendingReview; }
    public long getUnassignedLeads() { return unassignedLeads; }
}