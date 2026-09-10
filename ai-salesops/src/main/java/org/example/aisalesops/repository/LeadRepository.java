package org.example.aisalesops.repository;

import org.example.aisalesops.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository
        extends JpaRepository<Lead, Long> {

}