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

    @Query("""
    SELECT COUNT(l) FROM Lead l
    WHERE l.status = 'NEW'
      AND (:assignedUserId IS NULL OR l.assignedUser.id = :assignedUserId)
""")
    long countNewLeads(@Param("assignedUserId") Long assignedUserId);

    @Query("""
    SELECT COUNT(l) FROM Lead l
    WHERE l.scoreBand = 'HOT'
      AND (:assignedUserId IS NULL OR l.assignedUser.id = :assignedUserId)
""")
    long countHotLeads(@Param("assignedUserId") Long assignedUserId);

    @Query("""
    SELECT COUNT(l) FROM Lead l
    WHERE l.status = 'QUALIFIED'
      AND (:assignedUserId IS NULL OR l.assignedUser.id = :assignedUserId)
""")
    long countQualifiedLeads(@Param("assignedUserId") Long assignedUserId);

    @Query("""
    SELECT COUNT(l) FROM Lead l
    WHERE (:assignedUserId IS NULL OR l.assignedUser.id = :assignedUserId)
""")
    long countLeads(@Param("assignedUserId") Long assignedUserId);

    @Query("""
    SELECT COUNT(l) FROM Lead l
    WHERE l.assignedUser IS NULL
""")
    long countUnassignedLeads();

    @Query("""
    SELECT COALESCE(l.source, 'UNKNOWN'), COUNT(l)
    FROM Lead l
    WHERE (:assignedUserId IS NULL OR l.assignedUser.id = :assignedUserId)
    GROUP BY l.source
    ORDER BY COUNT(l) DESC
""")
    List<Object[]> countLeadsBySource(@Param("assignedUserId") Long assignedUserId);
}