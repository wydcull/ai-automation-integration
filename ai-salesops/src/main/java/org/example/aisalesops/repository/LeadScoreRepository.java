package org.example.aisalesops.repository;

import org.example.aisalesops.entity.LeadScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadScoreRepository
        extends JpaRepository<LeadScore, Long> {

    Optional<LeadScore> findByLeadId(
            Long leadId
    );
}