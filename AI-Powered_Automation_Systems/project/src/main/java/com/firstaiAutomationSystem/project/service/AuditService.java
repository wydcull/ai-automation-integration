package com.firstaiAutomationSystem.project.service;

import com.firstaiAutomationSystem.project.model.AuditLog;
import com.firstaiAutomationSystem.project.model.User;
import com.firstaiAutomationSystem.project.repository.AuditLogRepository;
import com.firstaiAutomationSystem.project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository,
                        UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * Log an action with automatic user detection from SecurityContext
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String entityType, Long entityId, Map<String, Object> details) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "system";

            User user = null;
            if (!"system".equals(username) && !"anonymousUser".equals(username)) {
                user = (User) userRepository.findByUsername(username).orElse(null);
            }

            AuditLog log = new AuditLog();
            log.setUser(user);
            log.setUsername(username);
            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setDetails(details);
            log.setTimestamp(LocalDateTime.now());
            log.setIpAddress(getCurrentIpAddress());

            auditLogRepository.save(log);
        } catch (Exception e) {
            // Don't fail the main transaction if audit logging fails
            System.err.println("Audit logging failed: " + e.getMessage());
        }
    }

    /**
     * Log an action for a specific user (when user is not in SecurityContext)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(Long userId, String action, String entityType, Long entityId,
                    Map<String, Object> details) {
        try {
            User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
            String username = user != null ? user.getUsername() : "system";

            AuditLog log = new AuditLog();
            log.setUser(user);
            log.setUsername(username);
            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setDetails(details);
            log.setTimestamp(LocalDateTime.now());
            log.setIpAddress(getCurrentIpAddress());

            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Audit logging failed: " + e.getMessage());
        }
    }

    /**
     * Log user login
     */
    public void logLogin(String username) {
        log(username, "USER_LOGIN", "USER", null, null);
    }

    /**
     * Log user logout
     */
    public void logLogout(String username) {
        log(username, "USER_LOGOUT", "USER", null, null);
    }

    /**
     * Log email approval
     */
    public void logEmailApproval(Long emailTriageId, String approvedBy) {
        log(approvedBy, "APPROVE_REPLY", "EMAIL_TRIAGE", emailTriageId,
                Map.of("approvedBy", approvedBy));
    }

    /**
     * Log email rejection
     */
    public void logEmailRejection(Long emailTriageId, String rejectedBy, String reason) {
        log(rejectedBy, "REJECT_REPLY", "EMAIL_TRIAGE", emailTriageId,
                Map.of("rejectedBy", rejectedBy, "reason", reason != null ? reason : ""));
    }

    /**
     * Log reply sent
     */
    public void logReplySent(Long emailTriageId, String sentBy, String gmailMessageId) {
        log(sentBy, "SEND_REPLY", "EMAIL_TRIAGE", emailTriageId,
                Map.of("sentBy", sentBy, "gmailMessageId", gmailMessageId));
    }

    /**
     * Log with username directly (for cases outside SecurityContext)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void log(String username, String action, String entityType, Long entityId,
                     Map<String, Object> details) {
        try {
            User user = (User) userRepository.findByUsername(username).orElse(null);

            AuditLog log = new AuditLog();
            log.setUser(user);
            log.setUsername(username);
            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setDetails(details);
            log.setTimestamp(LocalDateTime.now());
            log.setIpAddress(getCurrentIpAddress());

            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Audit logging failed: " + e.getMessage());
        }
    }

    /**
     * Get audit logs for a specific user
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsForUser(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * Get audit logs for a specific entity
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsForEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(
                entityType, entityId);
    }

    /**
     * Get recent audit logs (last N entries)
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs(int limit) {
        return auditLogRepository.findTopNByOrderByTimestampDesc(limit);
    }

    /**
     * Get audit logs by action type
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action);
    }

    /**
     * Get logs within a date range
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(startDate, endDate);
    }

    /**
     * Get current IP address (simplified - in production, extract from HttpServletRequest)
     */
    private String getCurrentIpAddress() {
        // In a real application, inject HttpServletRequest and extract IP
        // For now, return a placeholder
        return "127.0.0.1";
    }
}