package org.example.aisalesops.service;

import org.example.aisalesops.dto.DashboardSummaryResponse;
import org.example.aisalesops.dto.UserSummaryResponse;
import org.example.aisalesops.repository.DashboardEmailRepository;
import org.example.aisalesops.repository.DashboardLeadRepository;
import org.example.aisalesops.repository.DashboardUserRepository;
import org.example.aisalesops.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardUserRepository dashboardUserRepository;
    private final DashboardLeadRepository dashboardLeadRepository;
    private final DashboardEmailRepository dashboardEmailRepository;
    private final ProductRepository productRepository;

    public DashboardServiceImpl(
            DashboardUserRepository dashboardUserRepository,
            DashboardLeadRepository dashboardLeadRepository,
            DashboardEmailRepository dashboardEmailRepository,
            ProductRepository productRepository
    ) {
        this.dashboardUserRepository = dashboardUserRepository;
        this.dashboardLeadRepository = dashboardLeadRepository;
        this.dashboardEmailRepository = dashboardEmailRepository;
        this.productRepository = productRepository;
    }

    @Override
    public UserSummaryResponse getUserSummary() {

        long activeUsers =
                dashboardUserRepository.countActiveUsers();

        long managers =
                dashboardUserRepository.countActiveManagers();

        long representatives =
                dashboardUserRepository.countActiveRepresentatives();

        long admins =
                dashboardUserRepository.countActiveAdmins();

        return new UserSummaryResponse(
                activeUsers,
                managers,
                representatives,
                admins
        );
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary() {

        long activeUsers =
                dashboardUserRepository.countActiveUsers();

        long allLeads =
                dashboardLeadRepository.countAllLeads();

        long products =
                productRepository.countActiveProducts();

        long processedEmails =
                dashboardEmailRepository.countProcessedEmails();

        long aiFailed =
                dashboardEmailRepository.countFailedEmails();

        long pendingReview = 0;

        return new DashboardSummaryResponse(
                activeUsers,
                allLeads,
                products,
                processedEmails,
                aiFailed,
                pendingReview
        );
    }
}