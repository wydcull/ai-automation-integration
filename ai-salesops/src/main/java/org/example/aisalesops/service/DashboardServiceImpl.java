package org.example.aisalesops.service;

import org.example.aisalesops.dto.*;
import org.example.aisalesops.entity.Lead;
import org.example.aisalesops.repository.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardUserRepository dashboardUserRepository;
    private final DashboardLeadRepository dashboardLeadRepository;
    private final DashboardEmailRepository dashboardEmailRepository;
    private final ProductRepository productRepository;
    private final DashboardTaskRepository dashboardTaskRepository;

    public DashboardServiceImpl(
            DashboardUserRepository dashboardUserRepository,
            DashboardLeadRepository dashboardLeadRepository,
            DashboardEmailRepository dashboardEmailRepository,
            ProductRepository productRepository,
            DashboardTaskRepository dashboardTaskRepository
    ) {
        this.dashboardUserRepository = dashboardUserRepository;
        this.dashboardLeadRepository = dashboardLeadRepository;
        this.dashboardEmailRepository = dashboardEmailRepository;
        this.productRepository = productRepository;
        this.dashboardTaskRepository=dashboardTaskRepository;
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

    @Override
    public EmailStatsResponse getEmailStats() {

        long received =
                dashboardEmailRepository.countReceivedEmails();

        long processing =
                dashboardEmailRepository.countProcessingEmails();

        long processed =
                dashboardEmailRepository.countProcessedEmails();

        long failed =
                dashboardEmailRepository.countFailedEmails();

        return new EmailStatsResponse(
                received,
                processing,
                processed,
                failed
        );
    }

    @Override
    public List<StatusCountResponse> getLeadsByStatus() {
        return dashboardLeadRepository.countLeadsByStatus()
                .stream()
                .map(row -> new StatusCountResponse(
                        (String) row[0],
                        (Long) row[1]
                ))
                .toList();
    }
    @Override
    public List<ScoreBandCountResponse> getLeadsByScore() {
        return dashboardLeadRepository.countLeadsByScoreBand()
                .stream()
                .map(row -> new ScoreBandCountResponse(
                        (String) row[0],
                        (Long) row[1]
                ))
                .toList();
    }
    @Override
    public List<HotLeadResponse> getRecentHotLeads(int limit) {
        return dashboardLeadRepository.findRecentHotLeads()
                .stream()
                .limit(limit)
                .map(l -> new HotLeadResponse(
                        l.getId(),
                        l.getLeadCode(),
                        l.getCustomer() != null ? l.getCustomer().getCompanyName() : null,
                        l.getProductName(),
                        l.getScore(),
                        l.getScoreBand(),
                        l.getStatus(),
                        l.getAssignedUser() != null ? l.getAssignedUser().getFullName() : null,
                        l.getCreatedAt()
                ))
                .toList();
    }
    @Override
    public List<AssigneeLeadStatsResponse> getLeadsByAssignee() {
        OffsetDateTime now = OffsetDateTime.now();
        Map<Long, Long> openTasks = dashboardTaskRepository
                .countOpenTasksByAssignee()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
        Map<Long, Long> overdueTasks = dashboardTaskRepository
                .countOverdueTasksByAssignee(now)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
        return dashboardLeadRepository.countLeadsByAssignee()
                .stream()
                .map(row -> {
                    Long userId = (Long) row[0];
                    return new AssigneeLeadStatsResponse(
                            userId,
                            (String) row[1],
                            (Long) row[2],
                            ((Number) row[3]).longValue(),
                            ((Number) row[4]).longValue(),
                            openTasks.getOrDefault(userId, 0L),
                            overdueTasks.getOrDefault(userId, 0L)
                    );
                })
                .toList();
    }
    @Override
    public List<SlaBreachResponse> getSlaBreaches() {
        OffsetDateTime now = OffsetDateTime.now();
        return dashboardTaskRepository.findHotSlaBreaches(now)
                .stream()
                .map(t -> {
                    long hours = Duration.between(t.getDueAt(), now).toHours();
                    Lead l = t.getLead();
                    return new SlaBreachResponse(
                            l.getId(),
                            l.getLeadCode(),
                            l.getCustomer() != null ? l.getCustomer().getCompanyName() : null,
                            l.getScoreBand(),
                            t.getAssignedUser() != null ? t.getAssignedUser().getFullName() : null,
                            t.getDueAt(),
                            Math.max(hours, 0)
                    );
                })
                .toList();
    }
}