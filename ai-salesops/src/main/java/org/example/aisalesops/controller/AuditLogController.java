package org.example.aisalesops.controller;

import org.example.aisalesops.entity.AuditLog;
import org.example.aisalesops.service.AuditLogService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;


    // =========================================
    // CONSTRUCTOR
    // =========================================
    public AuditLogController(
            AuditLogService auditLogService
    ) {

        this.auditLogService = auditLogService;
    }


    // =========================================
    // CREATE - POST
    // =========================================
    @PostMapping
    public ResponseEntity<AuditLog> createAuditLog(
            @RequestBody AuditLog auditLog
    ) {

        AuditLog savedAuditLog =
                auditLogService.createAuditLog(
                        auditLog
                );

        return ResponseEntity.ok(
                savedAuditLog
        );
    }


    // =========================================
    // GET ALL
    // =========================================
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {

        List<AuditLog> auditLogs =
                auditLogService.getAllAuditLogs();

        return ResponseEntity.ok(
                auditLogs
        );
    }


    // =========================================
    // GET BY ID
    // =========================================
    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(
            @PathVariable Long id
    ) {

        Optional<AuditLog> auditLog =
                auditLogService.getAuditLogById(id);

        return auditLog
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================
    @PutMapping("/{id}")
    public ResponseEntity<AuditLog> updateAuditLog(
            @PathVariable Long id,
            @RequestBody AuditLog auditLog
    ) {

        AuditLog updatedAuditLog =
                auditLogService.updateAuditLog(
                        id,
                        auditLog
                );

        return ResponseEntity.ok(
                updatedAuditLog
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================
    @PatchMapping("/{id}")
    public ResponseEntity<AuditLog> partialUpdateAuditLog(
            @PathVariable Long id,
            @RequestBody AuditLog auditLog
    ) {

        AuditLog updatedAuditLog =
                auditLogService.partialUpdateAuditLog(
                        id,
                        auditLog
                );

        return ResponseEntity.ok(
                updatedAuditLog
        );
    }


    // =========================================
    // DELETE
    // =========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditLog(
            @PathVariable Long id
    ) {

        auditLogService.deleteAuditLog(
                id
        );

        return ResponseEntity.noContent().build();
    }

}