package org.example.aisalesops.repository;

import org.example.aisalesops.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository
        extends JpaRepository<SystemConfig, Long> {

}