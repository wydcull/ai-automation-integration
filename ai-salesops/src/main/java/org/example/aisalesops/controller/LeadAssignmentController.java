package org.example.aisalesops.controller;

import org.example.aisalesops.entity.LeadAssignment;
import org.example.aisalesops.service.LeadAssignmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lead-assignments")
public class LeadAssignmentController {

    private final LeadAssignmentService leadAssignmentService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public LeadAssignmentController(
            LeadAssignmentService leadAssignmentService
    ) {

        this.leadAssignmentService =
                leadAssignmentService;
    }


    // =========================================
    // CREATE LEAD ASSIGNMENT
    // =========================================

    @PostMapping
    public LeadAssignment createLeadAssignment(

            @RequestParam Long leadId,

            @RequestParam Long userId,

            @RequestParam(required = false) Long ruleId,

            @RequestBody LeadAssignment leadAssignment
    ) {

        return leadAssignmentService
                .createLeadAssignment(
                        leadId,
                        userId,
                        ruleId,
                        leadAssignment
                );
    }


    // =========================================
    // GET ALL LEAD ASSIGNMENTS
    // =========================================

    @GetMapping
    public List<LeadAssignment> getAllLeadAssignments() {

        return leadAssignmentService
                .getAllLeadAssignments();
    }


    // =========================================
    // GET LEAD ASSIGNMENT BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<LeadAssignment> getLeadAssignmentById(

            @PathVariable Long id
    ) {

        return leadAssignmentService
                .getLeadAssignmentById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =========================================
    // GET ASSIGNMENT HISTORY BY LEAD ID
    // =========================================

    @GetMapping("/lead/{leadId}")
    public List<LeadAssignment> getAssignmentsByLeadId(

            @PathVariable Long leadId
    ) {

        return leadAssignmentService
                .getAssignmentsByLeadId(leadId);
    }


    // =========================================
    // GET ASSIGNMENTS BY USER ID
    // =========================================

    @GetMapping("/user/{userId}")
    public List<LeadAssignment> getAssignmentsByUserId(

            @PathVariable Long userId
    ) {

        return leadAssignmentService
                .getAssignmentsByUserId(userId);
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @PutMapping("/{id}")
    public LeadAssignment updateLeadAssignment(

            @PathVariable Long id,

            @RequestParam Long leadId,

            @RequestParam Long userId,

            @RequestParam(required = false) Long ruleId,

            @RequestBody LeadAssignment leadAssignment
    ) {

        return leadAssignmentService
                .updateLeadAssignment(
                        id,
                        leadId,
                        userId,
                        ruleId,
                        leadAssignment
                );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @PatchMapping("/{id}")
    public LeadAssignment partialUpdateLeadAssignment(

            @PathVariable Long id,

            @RequestBody LeadAssignment leadAssignment
    ) {

        return leadAssignmentService
                .partialUpdateLeadAssignment(
                        id,
                        leadAssignment
                );
    }


    // =========================================
    // DELETE LEAD ASSIGNMENT
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeadAssignment(

            @PathVariable Long id
    ) {

        leadAssignmentService
                .deleteLeadAssignment(id);


        return ResponseEntity.ok(
                "Lead Assignment deleted successfully"
        );
    }
}