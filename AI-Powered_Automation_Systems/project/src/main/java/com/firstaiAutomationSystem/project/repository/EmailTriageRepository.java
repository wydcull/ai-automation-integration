package com.firstaiAutomationSystem.project.repository;

import com.firstaiAutomationSystem.project.model.EmailTriageRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmailTriageRepository extends JpaRepository<EmailTriageRecord, Long> {

    @Query("""
        SELECT r FROM EmailTriageRecord r
        WHERE (:category IS NULL OR r.category = :category)
          AND (:priority IS NULL OR r.priority = :priority)
        ORDER BY r.processedAt DESC
        """)
    Page<EmailTriageRecord> search(
            @Param("category") String category,
            @Param("priority") String priority,
            Pageable pageable
    );
}