package org.example.aisalesops.repository;

import org.example.aisalesops.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface DashboardTaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        SELECT t.assignedUser.id, COUNT(t)
        FROM Task t
        WHERE t.status = 'OPEN'
        GROUP BY t.assignedUser.id
    """)
    List<Object[]> countOpenTasksByAssignee();

    @Query("""
        SELECT t.assignedUser.id, COUNT(t)
        FROM Task t
        WHERE t.status = 'OPEN'
          AND t.dueAt < :now
        GROUP BY t.assignedUser.id
    """)
    List<Object[]> countOverdueTasksByAssignee(OffsetDateTime now);

    @Query("""
        SELECT t
        FROM Task t
        JOIN FETCH t.lead l
        LEFT JOIN FETCH l.customer c
        LEFT JOIN FETCH t.assignedUser u
        WHERE t.status = 'OPEN'
          AND t.dueAt < :now
          AND l.scoreBand = 'HOT'
        ORDER BY t.dueAt ASC
    """)
    List<Task> findHotSlaBreaches(OffsetDateTime now);
}