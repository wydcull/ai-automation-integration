package org.example.aisalesops.repository;

import org.example.aisalesops.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DashboardLeadRepository extends JpaRepository<Lead, Long> {

    @Query("""
        SELECT COUNT(l)
        FROM Lead l
    """)
    long countAllLeads();
}