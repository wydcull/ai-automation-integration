package org.example.aisalesops.service;

import org.example.aisalesops.dto.DashboardSummaryResponse;
import org.example.aisalesops.dto.UserSummaryResponse;

public interface DashboardService {

    UserSummaryResponse getUserSummary();

    DashboardSummaryResponse getDashboardSummary();
}