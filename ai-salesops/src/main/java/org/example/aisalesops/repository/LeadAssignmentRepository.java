package org.example.aisalesops.repository;

import org.example.aisalesops.entity.LeadAssignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadAssignmentRepository
        extends JpaRepository<LeadAssignment, Long> {

    List<LeadAssignment> findByLeadId(
            Long leadId
    );


    List<LeadAssignment> findByAssignedUserId(
            Long assignedUserId
    );
}