package org.example.aisalesops.controller;

import org.example.aisalesops.entity.LeadScore;
import org.example.aisalesops.service.LeadScoreService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lead-scores")
public class LeadScoreController {

    private final LeadScoreService leadScoreService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public LeadScoreController(
            LeadScoreService leadScoreService
    ) {

        this.leadScoreService =
                leadScoreService;
    }


    // =========================================
    // CREATE LEAD SCORE
    // =========================================

    @PostMapping("/lead/{leadId}")
    public LeadScore createLeadScore(

            @PathVariable Long leadId,

            @RequestBody LeadScore leadScore
    ) {

        return leadScoreService.createLeadScore(
                leadId,
                leadScore
        );
    }


    // =========================================
    // GET ALL LEAD SCORES
    // =========================================

    @GetMapping
    public List<LeadScore> getAllLeadScores() {

        return leadScoreService
                .getAllLeadScores();
    }


    // =========================================
    // GET LEAD SCORE BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<LeadScore> getLeadScoreById(

            @PathVariable Long id
    ) {

        return leadScoreService
                .getLeadScoreById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =========================================
    // GET SCORE BY LEAD ID
    // =========================================

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<LeadScore> getLeadScoreByLeadId(

            @PathVariable Long leadId
    ) {

        return leadScoreService
                .getLeadScoreByLeadId(leadId)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @PutMapping("/{id}")
    public LeadScore updateLeadScore(

            @PathVariable Long id,

            @RequestBody LeadScore leadScore
    ) {

        return leadScoreService
                .updateLeadScore(
                        id,
                        leadScore
                );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @PatchMapping("/{id}")
    public LeadScore partialUpdateLeadScore(

            @PathVariable Long id,

            @RequestBody LeadScore leadScore
    ) {

        return leadScoreService
                .partialUpdateLeadScore(
                        id,
                        leadScore
                );
    }


    // =========================================
    // DELETE LEAD SCORE
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeadScore(

            @PathVariable Long id
    ) {

        leadScoreService
                .deleteLeadScore(id);


        return ResponseEntity.ok(
                "Lead Score deleted successfully"
        );
    }
}