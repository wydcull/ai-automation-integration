package org.example.aisalesops.repository;

import org.example.aisalesops.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardLeadRepository extends JpaRepository<Lead, Long> {

    @Query("""
        SELECT COUNT(l)
        FROM Lead l
    """)
    long countAllLeads();

    @Query("""
        SELECT l.status, COUNT(l)
        FROM Lead l
        GROUP BY l.status
        ORDER BY l.status
    """)
    List<Object[]> countLeadsByStatus();

    @Query("""
        SELECT COALESCE(l.scoreBand, 'UNKNOWN'), COUNT(l)
        FROM Lead l
        GROUP BY l.scoreBand
        ORDER BY COUNT(l) DESC
    """)
    List<Object[]> countLeadsByScoreBand();

    @Query("""
        SELECT l
        FROM Lead l
        LEFT JOIN FETCH l.customer c
        LEFT JOIN FETCH l.assignedUser u
        WHERE l.scoreBand = 'HOT'
        ORDER BY l.createdAt DESC
    """)
    List<Lead> findRecentHotLeads();

    @Query("""
        SELECT u.id,
               u.fullName,
               COUNT(l),
               SUM(CASE WHEN l.scoreBand = 'HOT' THEN 1 ELSE 0 END),
               SUM(CASE WHEN l.status = 'NEW' THEN 1 ELSE 0 END)
        FROM Lead l
        JOIN l.assignedUser u
        GROUP BY u.id, u.fullName
        ORDER BY COUNT(l) DESC
    """)
    List<Object[]> countLeadsByAssignee();
}