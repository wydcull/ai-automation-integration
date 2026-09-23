package org.example.aisalesops.controller;

import org.example.aisalesops.dto.*;
import org.example.aisalesops.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/users-summary")
    public ResponseEntity<UserSummaryResponse> getUserSummary() {
        return ResponseEntity.ok(dashboardService.getUserSummary());
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/email-stats")
    public ResponseEntity<EmailStatsResponse> getEmailStats() {
        return ResponseEntity.ok(dashboardService.getEmailStats());
    }

    @GetMapping("/leads-by-status")
    public ResponseEntity<List<StatusCountResponse>> getLeadsByStatus() {
        return ResponseEntity.ok(dashboardService.getLeadsByStatus());
    }

    @GetMapping("/leads-by-score")
    public ResponseEntity<List<ScoreBandCountResponse>> getLeadsByScore() {
        return ResponseEntity.ok(dashboardService.getLeadsByScore());
    }

    @GetMapping("/recent-hot-leads")
    public ResponseEntity<List<HotLeadResponse>> getRecentHotLeads(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(dashboardService.getRecentHotLeads(limit));
    }

    @GetMapping("/leads-by-assignee")
    public ResponseEntity<List<AssigneeLeadStatsResponse>> getLeadsByAssignee() {
        return ResponseEntity.ok(dashboardService.getLeadsByAssignee());
    }

    @GetMapping("/sla-breaches")
    public ResponseEntity<List<SlaBreachResponse>> getSlaBreaches() {
        return ResponseEntity.ok(dashboardService.getSlaBreaches());
    }
}