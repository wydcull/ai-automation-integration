package org.example.aisalesops.dto;

public class DashboardSummaryResponse {

    private long activeUsers;
    private long allLeads;
    private long products;
    private long processedEmails;
    private long aiFailed;
    private long pendingReview;

    public DashboardSummaryResponse(long activeUsers,
                                    long allLeads,
                                    long products,
                                    long processedEmails,
                                    long aiFailed,
                                    long pendingReview) {

        this.activeUsers = activeUsers;
        this.allLeads = allLeads;
        this.products = products;
        this.processedEmails = processedEmails;
        this.aiFailed = aiFailed;
        this.pendingReview = pendingReview;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getAllLeads() {
        return allLeads;
    }

    public void setAllLeads(long allLeads) {
        this.allLeads = allLeads;
    }

    public long getProducts() {
        return products;
    }

    public void setProducts(long products) {
        this.products = products;
    }

    public long getProcessedEmails() {
        return processedEmails;
    }

    public void setProcessedEmails(long processedEmails) {
        this.processedEmails = processedEmails;
    }

    public long getAiFailed() {
        return aiFailed;
    }

    public void setAiFailed(long aiFailed) {
        this.aiFailed = aiFailed;
    }

    public long getPendingReview() {
        return pendingReview;
    }

    public void setPendingReview(long pendingReview) {
        this.pendingReview = pendingReview;
    }
}