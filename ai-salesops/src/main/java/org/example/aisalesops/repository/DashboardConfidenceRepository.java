package org.example.aisalesops.repository;

import org.example.aisalesops.entity.ExtractionFieldConfidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DashboardConfidenceRepository
        extends JpaRepository<ExtractionFieldConfidence, Long> {

    @Query("""
        SELECT COUNT(DISTINCT e.leadId)
        FROM ExtractionFieldConfidence e
        WHERE e.flagged = true
    """)
    long countPendingReviewLeads();

    @Query("""
        SELECT e.leadId, e.fieldName, e.confidence, e.aiValue
        FROM ExtractionFieldConfidence e
        WHERE e.flagged = true
        ORDER BY e.confidence ASC
    """)
    List<Object[]> findFlaggedFields();
}