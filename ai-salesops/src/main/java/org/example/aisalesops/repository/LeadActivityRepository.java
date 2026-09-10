package org.example.aisalesops.repository;

import org.example.aisalesops.entity.LeadActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadActivityRepository
        extends JpaRepository<LeadActivity, Long> {

}