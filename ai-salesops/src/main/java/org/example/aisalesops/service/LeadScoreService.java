package org.example.aisalesops.service;

import org.example.aisalesops.entity.Lead;
import org.example.aisalesops.entity.LeadScore;
import org.example.aisalesops.repository.LeadRepository;
import org.example.aisalesops.repository.LeadScoreRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LeadScoreService {

    private final LeadScoreRepository leadScoreRepository;

    private final LeadRepository leadRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public LeadScoreService(
            LeadScoreRepository leadScoreRepository,
            LeadRepository leadRepository
    ) {

        this.leadScoreRepository =
                leadScoreRepository;

        this.leadRepository =
                leadRepository;
    }


    // =========================================
    // CREATE LEAD SCORE - POST
    // =========================================

    public LeadScore createLeadScore(
            Long leadId,
            LeadScore leadScore
    ) {

        Lead lead =
                leadRepository.findById(leadId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead not found with id: "
                                                + leadId
                                )
                        );


        leadScore.setLead(lead);


        return leadScoreRepository.save(
                leadScore
        );
    }


    // =========================================
    // GET ALL LEAD SCORES - GET
    // =========================================

    public List<LeadScore> getAllLeadScores() {

        return leadScoreRepository.findAll();
    }


    // =========================================
    // GET LEAD SCORE BY ID - GET
    // =========================================

    public Optional<LeadScore> getLeadScoreById(
            Long id
    ) {

        return leadScoreRepository.findById(id);
    }


    // =========================================
    // GET LEAD SCORE BY LEAD ID
    // =========================================

    public Optional<LeadScore> getLeadScoreByLeadId(
            Long leadId
    ) {

        return leadScoreRepository.findByLeadId(
                leadId
        );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @Transactional
    public LeadScore updateLeadScore(
            Long id,
            LeadScore updatedLeadScore
    ) {

        LeadScore existingLeadScore =
                leadScoreRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead Score not found with id: "
                                                + id
                                )
                        );


        existingLeadScore.setTotalScore(
                updatedLeadScore.getTotalScore()
        );

        existingLeadScore.setPurchaseIntent(
                updatedLeadScore.getPurchaseIntent()
        );

        existingLeadScore.setCompanyFit(
                updatedLeadScore.getCompanyFit()
        );

        existingLeadScore.setBudgetPts(
                updatedLeadScore.getBudgetPts()
        );

        existingLeadScore.setTimelinePts(
                updatedLeadScore.getTimelinePts()
        );

        existingLeadScore.setProductMatch(
                updatedLeadScore.getProductMatch()
        );

        existingLeadScore.setEngagement(
                updatedLeadScore.getEngagement()
        );

        existingLeadScore.setExplanation(
                updatedLeadScore.getExplanation()
        );

        existingLeadScore.setModelName(
                updatedLeadScore.getModelName()
        );


        // lead and createdAt are not changed

        return leadScoreRepository.save(
                existingLeadScore
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @Transactional
    public LeadScore partialUpdateLeadScore(
            Long id,
            LeadScore updatedLeadScore
    ) {

        LeadScore existingLeadScore =
                leadScoreRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead Score not found with id: "
                                                + id
                                )
                        );


        if (updatedLeadScore.getTotalScore() != null) {

            existingLeadScore.setTotalScore(
                    updatedLeadScore.getTotalScore()
            );
        }


        if (updatedLeadScore.getPurchaseIntent() != null) {

            existingLeadScore.setPurchaseIntent(
                    updatedLeadScore.getPurchaseIntent()
            );
        }


        if (updatedLeadScore.getCompanyFit() != null) {

            existingLeadScore.setCompanyFit(
                    updatedLeadScore.getCompanyFit()
            );
        }


        if (updatedLeadScore.getBudgetPts() != null) {

            existingLeadScore.setBudgetPts(
                    updatedLeadScore.getBudgetPts()
            );
        }


        if (updatedLeadScore.getTimelinePts() != null) {

            existingLeadScore.setTimelinePts(
                    updatedLeadScore.getTimelinePts()
            );
        }


        if (updatedLeadScore.getProductMatch() != null) {

            existingLeadScore.setProductMatch(
                    updatedLeadScore.getProductMatch()
            );
        }


        if (updatedLeadScore.getEngagement() != null) {

            existingLeadScore.setEngagement(
                    updatedLeadScore.getEngagement()
            );
        }


        if (updatedLeadScore.getExplanation() != null) {

            existingLeadScore.setExplanation(
                    updatedLeadScore.getExplanation()
            );
        }


        if (updatedLeadScore.getModelName() != null) {

            existingLeadScore.setModelName(
                    updatedLeadScore.getModelName()
            );
        }


        return leadScoreRepository.save(
                existingLeadScore
        );
    }


    // =========================================
    // DELETE LEAD SCORE
    // =========================================

    public void deleteLeadScore(
            Long id
    ) {

        LeadScore existingLeadScore =
                leadScoreRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lead Score not found with id: "
                                                + id
                                )
                        );


        leadScoreRepository.delete(
                existingLeadScore
        );
    }
}