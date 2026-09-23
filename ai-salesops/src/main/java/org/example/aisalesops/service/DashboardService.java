package org.example.aisalesops.service;

import org.example.aisalesops.dto.*;

import java.util.List;

public interface DashboardService {

    UserSummaryResponse getUserSummary();

    DashboardSummaryResponse getDashboardSummary();

    EmailStatsResponse getEmailStats();

    List<StatusCountResponse> getLeadsByStatus();

    List<ScoreBandCountResponse> getLeadsByScore();

    List<HotLeadResponse> getRecentHotLeads(int limit);

    List<AssigneeLeadStatsResponse> getLeadsByAssignee();

    List<SlaBreachResponse> getSlaBreaches();
}