package org.example.aisalesops.service;

import org.example.aisalesops.entity.AssignmentRule;
import org.example.aisalesops.entity.Lead;
import org.example.aisalesops.entity.LeadAssignment;
import org.example.aisalesops.entity.User;

import org.example.aisalesops.repository.AssignmentRuleRepository;
import org.example.aisalesops.repository.LeadAssignmentRepository;
import org.example.aisalesops.repository.LeadRepository;
import org.example.aisalesops.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LeadAssignmentService {

    private final LeadAssignmentRepository leadAssignmentRepository;

    private final LeadRepository leadRepository;

    private final UserRepository userRepository;

    private final AssignmentRuleRepository assignmentRuleRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public LeadAssignmentService(
            LeadAssignmentRepository leadAssignmentRepository,
            LeadRepository leadRepository,
            UserRepository userRepository,
            AssignmentRuleRepository assignmentRuleRepository
    ) {

        this.leadAssignmentRepository =
                leadAssignmentRepository;

        this.leadRepository =
                leadRepository;

        this.userRepository =
                userRepository;

        this.assignmentRuleRepository =
                assignmentRuleRepository;
    }


    // =========================================
    // CREATE LEAD ASSIGNMENT - POST
    // =========================================

    @Transactional
    public LeadAssignment createLeadAssignment(
            Long leadId,
            Long userId,
            Long ruleId,
            LeadAssignment leadAssignment
    ) {

        // Find Lead
        Lead lead =
                leadRepository.findById(leadId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead not found with id: "
                                                + leadId
                                )
                        );


        // Find User
        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found with id: "
                                                + userId
                                )
                        );


        leadAssignment.setLead(lead);

        leadAssignment.setAssignedUser(user);


        // Assignment Rule is optional
        if (ruleId != null) {

            AssignmentRule assignmentRule =
                    assignmentRuleRepository.findById(ruleId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Assignment Rule not found with id: "
                                                    + ruleId
                                    )
                            );

            leadAssignment.setAssignmentRule(
                    assignmentRule
            );
        }


        // Update current assigned user in Lead table
        lead.setAssignedUser(user);

        leadRepository.save(lead);


        // Save assignment history
        return leadAssignmentRepository.save(
                leadAssignment
        );
    }


    // =========================================
    // GET ALL LEAD ASSIGNMENTS
    // =========================================

    public List<LeadAssignment> getAllLeadAssignments() {

        return leadAssignmentRepository.findAll();
    }


    // =========================================
    // GET LEAD ASSIGNMENT BY ID
    // =========================================

    public Optional<LeadAssignment> getLeadAssignmentById(
            Long id
    ) {

        return leadAssignmentRepository.findById(id);
    }


    // =========================================
    // GET ASSIGNMENT HISTORY BY LEAD ID
    // =========================================

    public List<LeadAssignment> getAssignmentsByLeadId(
            Long leadId
    ) {

        return leadAssignmentRepository.findByLeadId(
                leadId
        );
    }


    // =========================================
    // GET ASSIGNMENTS BY USER ID
    // =========================================

    public List<LeadAssignment> getAssignmentsByUserId(
            Long userId
    ) {

        return leadAssignmentRepository
                .findByAssignedUserId(userId);
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @Transactional
    public LeadAssignment updateLeadAssignment(
            Long id,
            Long leadId,
            Long userId,
            Long ruleId,
            LeadAssignment updatedLeadAssignment
    ) {

        LeadAssignment existingAssignment =
                leadAssignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead Assignment not found with id: "
                                                + id
                                )
                        );


        // Update Lead
        Lead lead =
                leadRepository.findById(leadId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead not found with id: "
                                                + leadId
                                )
                        );


        // Update User
        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found with id: "
                                                + userId
                                )
                        );


        existingAssignment.setLead(lead);

        existingAssignment.setAssignedUser(user);

        existingAssignment.setReason(
                updatedLeadAssignment.getReason()
        );


        // Update Rule
        if (ruleId != null) {

            AssignmentRule assignmentRule =
                    assignmentRuleRepository.findById(ruleId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Assignment Rule not found with id: "
                                                    + ruleId
                                    )
                            );

            existingAssignment.setAssignmentRule(
                    assignmentRule
            );

        } else {

            existingAssignment.setAssignmentRule(null);
        }


        // Update current assigned user in Lead table
        lead.setAssignedUser(user);

        leadRepository.save(lead);


        // assignedAt is not changed

        return leadAssignmentRepository.save(
                existingAssignment
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @Transactional
    public LeadAssignment partialUpdateLeadAssignment(
            Long id,
            LeadAssignment updatedLeadAssignment
    ) {

        LeadAssignment existingAssignment =
                leadAssignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead Assignment not found with id: "
                                                + id
                                )
                        );


        // Update Reason only if provided
        if (updatedLeadAssignment.getReason() != null) {

            existingAssignment.setReason(
                    updatedLeadAssignment.getReason()
            );
        }


        // Update Lead if provided
        if (
                updatedLeadAssignment.getLead() != null
                        &&
                        updatedLeadAssignment
                                .getLead()
                                .getId() != null
        ) {

            Long leadId =
                    updatedLeadAssignment
                            .getLead()
                            .getId();


            Lead lead =
                    leadRepository.findById(leadId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Lead not found with id: "
                                                    + leadId
                                    )
                            );


            existingAssignment.setLead(lead);
        }


        // Update Assigned User if provided
        if (
                updatedLeadAssignment.getAssignedUser() != null
                        &&
                        updatedLeadAssignment
                                .getAssignedUser()
                                .getId() != null
        ) {

            Long userId =
                    updatedLeadAssignment
                            .getAssignedUser()
                            .getId();


            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found with id: "
                                                    + userId
                                    )
                            );


            existingAssignment.setAssignedUser(user);


            // Update current assigned user
            existingAssignment
                    .getLead()
                    .setAssignedUser(user);

            leadRepository.save(
                    existingAssignment.getLead()
            );
        }


        // Update Assignment Rule if provided
        if (
                updatedLeadAssignment.getAssignmentRule() != null
                        &&
                        updatedLeadAssignment
                                .getAssignmentRule()
                                .getId() != null
        ) {

            Long ruleId =
                    updatedLeadAssignment
                            .getAssignmentRule()
                            .getId();


            AssignmentRule assignmentRule =
                    assignmentRuleRepository.findById(ruleId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Assignment Rule not found with id: "
                                                    + ruleId
                                    )
                            );


            existingAssignment.setAssignmentRule(
                    assignmentRule
            );
        }


        // assignedAt is intentionally not updated

        return leadAssignmentRepository.save(
                existingAssignment
        );
    }


    // =========================================
    // DELETE LEAD ASSIGNMENT
    // =========================================

    public void deleteLeadAssignment(
            Long id
    ) {

        LeadAssignment existingAssignment =
                leadAssignmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead Assignment not found with id: "
                                                + id
                                )
                        );


        leadAssignmentRepository.delete(
                existingAssignment
        );
    }
}