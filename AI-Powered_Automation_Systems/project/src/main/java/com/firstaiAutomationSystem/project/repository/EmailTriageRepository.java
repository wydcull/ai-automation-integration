package com.firstaiAutomationSystem.project.repository;

import com.firstaiAutomationSystem.project.model.EmailTriageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTriageRepository extends JpaRepository<EmailTriageRecord, Long> {
}