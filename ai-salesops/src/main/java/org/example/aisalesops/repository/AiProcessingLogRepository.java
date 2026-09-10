package org.example.aisalesops.repository;

import org.example.aisalesops.entity.AiProcessingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiProcessingLogRepository
        extends JpaRepository<AiProcessingLog, Long> {

}