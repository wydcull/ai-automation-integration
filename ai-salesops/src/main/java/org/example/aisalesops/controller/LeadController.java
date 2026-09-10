package org.example.aisalesops.controller;

import org.example.aisalesops.entity.Lead;
import org.example.aisalesops.service.LeadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public LeadController(
            LeadService leadService
    ) {
        this.leadService = leadService;
    }


    // =========================================
    // CREATE LEAD - POST
    // =========================================

    @PostMapping
    public Lead createLead(
            @RequestBody Lead lead
    ) {

        return leadService.createLead(
                lead
        );
    }


    // =========================================
    // GET ALL LEADS
    // =========================================

    @GetMapping
    public List<Lead> getAllLeads() {

        return leadService.getAllLeads();
    }


    // =========================================
    // GET LEAD BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(
            @PathVariable Long id
    ) {

        return leadService.getLeadById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @PutMapping("/{id}")
    public Lead updateLead(
            @PathVariable Long id,
            @RequestBody Lead lead
    ) {

        return leadService.updateLead(
                id,
                lead
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @PatchMapping("/{id}")
    public Lead partialUpdateLead(
            @PathVariable Long id,
            @RequestBody Lead lead
    ) {

        return leadService.partialUpdateLead(
                id,
                lead
        );
    }


    // =========================================
    // DELETE LEAD
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLead(
            @PathVariable Long id
    ) {

        leadService.deleteLead(id);

        return ResponseEntity.ok(
                "Lead deleted successfully"
        );
    }

}