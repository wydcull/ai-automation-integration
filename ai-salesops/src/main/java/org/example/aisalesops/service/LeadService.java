package org.example.aisalesops.service;

import org.example.aisalesops.entity.Lead;
import org.example.aisalesops.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LeadService {

    private final LeadRepository leadRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public LeadService(
            LeadRepository leadRepository
    ) {
        this.leadRepository = leadRepository;
    }


    // =========================================
    // CREATE LEAD - POST
    // =========================================

    public Lead createLead(
            Lead lead
    ) {

        return leadRepository.save(lead);
    }


    // =========================================
    // GET ALL LEADS
    // =========================================

    public List<Lead> getAllLeads() {

        return leadRepository.findAll();
    }


    // =========================================
    // GET LEAD BY ID
    // =========================================

    public Optional<Lead> getLeadById(
            Long id
    ) {

        return leadRepository.findById(id);
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @Transactional
    public Lead updateLead(
            Long id,
            Lead updatedLead
    ) {

        Lead existingLead =
                leadRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead not found with id: " + id
                                )
                        );


        existingLead.setLeadCode(
                updatedLead.getLeadCode()
        );

        existingLead.setCustomer(
                updatedLead.getCustomer()
        );

        existingLead.setProductName(
                updatedLead.getProductName()
        );

        existingLead.setProductModel(
                updatedLead.getProductModel()
        );

        existingLead.setQuantity(
                updatedLead.getQuantity()
        );

        existingLead.setUnit(
                updatedLead.getUnit()
        );

        existingLead.setSpecifications(
                updatedLead.getSpecifications()
        );

        existingLead.setBudget(
                updatedLead.getBudget()
        );

        existingLead.setDeliveryDays(
                updatedLead.getDeliveryDays()
        );

        existingLead.setIntent(
                updatedLead.getIntent()
        );

        existingLead.setUrgency(
                updatedLead.getUrgency()
        );

        existingLead.setStatus(
                updatedLead.getStatus()
        );

        existingLead.setScore(
                updatedLead.getScore()
        );

        existingLead.setScoreBand(
                updatedLead.getScoreBand()
        );

        existingLead.setSource(
                updatedLead.getSource()
        );

        existingLead.setAssignedUser(
                updatedLead.getAssignedUser()
        );

        existingLead.setEmailMessage(
                updatedLead.getEmailMessage()
        );

        existingLead.setAiSummary(
                updatedLead.getAiSummary()
        );

        existingLead.setRecommendedAction(
                updatedLead.getRecommendedAction()
        );


        // createdAt and updatedAt handled automatically

        return leadRepository.save(
                existingLead
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @Transactional
    public Lead partialUpdateLead(
            Long id,
            Lead updatedLead
    ) {

        Lead existingLead =
                leadRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead not found with id: " + id
                                )
                        );


        if (updatedLead.getLeadCode() != null) {
            existingLead.setLeadCode(
                    updatedLead.getLeadCode()
            );
        }

        if (updatedLead.getCustomer() != null) {
            existingLead.setCustomer(
                    updatedLead.getCustomer()
            );
        }

        if (updatedLead.getProductName() != null) {
            existingLead.setProductName(
                    updatedLead.getProductName()
            );
        }

        if (updatedLead.getProductModel() != null) {
            existingLead.setProductModel(
                    updatedLead.getProductModel()
            );
        }

        if (updatedLead.getQuantity() != null) {
            existingLead.setQuantity(
                    updatedLead.getQuantity()
            );
        }

        if (updatedLead.getUnit() != null) {
            existingLead.setUnit(
                    updatedLead.getUnit()
            );
        }

        if (updatedLead.getSpecifications() != null) {
            existingLead.setSpecifications(
                    updatedLead.getSpecifications()
            );
        }

        if (updatedLead.getBudget() != null) {
            existingLead.setBudget(
                    updatedLead.getBudget()
            );
        }

        if (updatedLead.getDeliveryDays() != null) {
            existingLead.setDeliveryDays(
                    updatedLead.getDeliveryDays()
            );
        }

        if (updatedLead.getIntent() != null) {
            existingLead.setIntent(
                    updatedLead.getIntent()
            );
        }

        if (updatedLead.getUrgency() != null) {
            existingLead.setUrgency(
                    updatedLead.getUrgency()
            );
        }

        if (updatedLead.getStatus() != null) {
            existingLead.setStatus(
                    updatedLead.getStatus()
            );
        }

        if (updatedLead.getScore() != null) {
            existingLead.setScore(
                    updatedLead.getScore()
            );
        }

        if (updatedLead.getScoreBand() != null) {
            existingLead.setScoreBand(
                    updatedLead.getScoreBand()
            );
        }

        if (updatedLead.getSource() != null) {
            existingLead.setSource(
                    updatedLead.getSource()
            );
        }

        if (updatedLead.getAssignedUser() != null) {
            existingLead.setAssignedUser(
                    updatedLead.getAssignedUser()
            );
        }

        if (updatedLead.getEmailMessage() != null) {
            existingLead.setEmailMessage(
                    updatedLead.getEmailMessage()
            );
        }

        if (updatedLead.getAiSummary() != null) {
            existingLead.setAiSummary(
                    updatedLead.getAiSummary()
            );
        }

        if (updatedLead.getRecommendedAction() != null) {
            existingLead.setRecommendedAction(
                    updatedLead.getRecommendedAction()
            );
        }


        return leadRepository.save(
                existingLead
        );
    }


    // =========================================
    // DELETE LEAD
    // =========================================

    public void deleteLead(
            Long id
    ) {

        Lead existingLead =
                leadRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead not found with id: " + id
                                )
                        );

        leadRepository.delete(
                existingLead
        );
    }

}