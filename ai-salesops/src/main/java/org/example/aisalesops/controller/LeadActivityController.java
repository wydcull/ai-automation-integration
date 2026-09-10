package org.example.aisalesops.controller;

import org.example.aisalesops.entity.LeadActivity;
import org.example.aisalesops.service.LeadActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lead-activities")
public class LeadActivityController {

    private final LeadActivityService leadActivityService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public LeadActivityController(
            LeadActivityService leadActivityService
    ) {

        this.leadActivityService =
                leadActivityService;
    }


    // =========================================
    // CREATE - POST
    // =========================================

    @PostMapping
    public LeadActivity createLeadActivity(
            @RequestBody LeadActivity leadActivity
    ) {

        return leadActivityService
                .createLeadActivity(
                        leadActivity
                );
    }


    // =========================================
    // GET ALL
    // =========================================

    @GetMapping
    public List<LeadActivity> getAllLeadActivities() {

        return leadActivityService
                .getAllLeadActivities();
    }


    // =========================================
    // GET BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<LeadActivity> getLeadActivityById(
            @PathVariable Long id
    ) {

        return leadActivityService
                .getLeadActivityById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @PutMapping("/{id}")
    public LeadActivity updateLeadActivity(
            @PathVariable Long id,
            @RequestBody LeadActivity leadActivity
    ) {

        return leadActivityService
                .updateLeadActivity(
                        id,
                        leadActivity
                );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @PatchMapping("/{id}")
    public LeadActivity partialUpdateLeadActivity(
            @PathVariable Long id,
            @RequestBody LeadActivity leadActivity
    ) {

        return leadActivityService
                .partialUpdateLeadActivity(
                        id,
                        leadActivity
                );
    }


    // =========================================
    // DELETE
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeadActivity(
            @PathVariable Long id
    ) {

        leadActivityService.deleteLeadActivity(id);


        return ResponseEntity.ok(
                "Lead Activity deleted successfully"
        );
    }
}