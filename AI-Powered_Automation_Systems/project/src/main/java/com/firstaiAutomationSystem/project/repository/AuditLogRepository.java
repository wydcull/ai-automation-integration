package com.firstaiAutomationSystem.project.repository;

import com.firstaiAutomationSystem.project.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find all logs for a specific user
     */
    List<AuditLog> findByUserIdOrderByTimestampDesc(Long userId);

    /**
     * Find logs by action type
     */
    List<AuditLog> findByActionOrderByTimestampDesc(String action);

    /**
     * Find logs for a specific entity
     */
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
            String entityType, Long entityId);

    /**
     * Find logs within a date range
     */
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find recent N logs
     */
    @Query("SELECT a FROM AuditLog a ORDER BY a.timestamp DESC")
    List<AuditLog> findTopNByOrderByTimestampDesc(@Param("limit") int limit);

    /**
     * Count actions by user
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.user.id = :userId AND a.action = :action")
    Long countByUserIdAndAction(@Param("userId") Long userId, @Param("action") String action);

    /**
     * Find logs by username (for system users)
     */
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);
}