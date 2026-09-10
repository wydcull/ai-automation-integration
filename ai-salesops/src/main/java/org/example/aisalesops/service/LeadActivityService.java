package org.example.aisalesops.service;

import org.example.aisalesops.entity.LeadActivity;
import org.example.aisalesops.repository.LeadActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LeadActivityService {

    private final LeadActivityRepository leadActivityRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public LeadActivityService(
            LeadActivityRepository leadActivityRepository
    ) {

        this.leadActivityRepository =
                leadActivityRepository;
    }


    // =========================================
    // CREATE - POST
    // =========================================

    public LeadActivity createLeadActivity(
            LeadActivity leadActivity
    ) {

        return leadActivityRepository.save(
                leadActivity
        );
    }


    // =========================================
    // GET ALL
    // =========================================

    public List<LeadActivity> getAllLeadActivities() {

        return leadActivityRepository.findAll();
    }


    // =========================================
    // GET BY ID
    // =========================================

    public Optional<LeadActivity> getLeadActivityById(
            Long id
    ) {

        return leadActivityRepository.findById(id);
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @Transactional
    public LeadActivity updateLeadActivity(
            Long id,
            LeadActivity updatedLeadActivity
    ) {

        LeadActivity existingLeadActivity =
                leadActivityRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead Activity not found with id: "
                                                + id
                                )
                        );


        // Update Lead
        existingLeadActivity.setLead(
                updatedLeadActivity.getLead()
        );


        // Update User
        existingLeadActivity.setUser(
                updatedLeadActivity.getUser()
        );


        // Update Activity Type
        existingLeadActivity.setActivityType(
                updatedLeadActivity.getActivityType()
        );


        // Update Content
        existingLeadActivity.setContent(
                updatedLeadActivity.getContent()
        );


        // createdAt is not updated

        return leadActivityRepository.save(
                existingLeadActivity
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @Transactional
    public LeadActivity partialUpdateLeadActivity(
            Long id,
            LeadActivity updatedLeadActivity
    ) {

        LeadActivity existingLeadActivity =
                leadActivityRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead Activity not found with id: "
                                                + id
                                )
                        );


        // Update Lead
        if (updatedLeadActivity.getLead() != null) {

            existingLeadActivity.setLead(
                    updatedLeadActivity.getLead()
            );
        }


        // Update User
        if (updatedLeadActivity.getUser() != null) {

            existingLeadActivity.setUser(
                    updatedLeadActivity.getUser()
            );
        }


        // Update Activity Type
        if (updatedLeadActivity.getActivityType() != null) {

            existingLeadActivity.setActivityType(
                    updatedLeadActivity.getActivityType()
            );
        }


        // Update Content
        if (updatedLeadActivity.getContent() != null) {

            existingLeadActivity.setContent(
                    updatedLeadActivity.getContent()
            );
        }


        // createdAt is intentionally not updated

        return leadActivityRepository.save(
                existingLeadActivity
        );
    }


    // =========================================
    // DELETE
    // =========================================

    public void deleteLeadActivity(
            Long id
    ) {

        LeadActivity existingLeadActivity =
                leadActivityRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead Activity not found with id: "
                                                + id
                                )
                        );


        leadActivityRepository.delete(
                existingLeadActivity
        );
    }
}