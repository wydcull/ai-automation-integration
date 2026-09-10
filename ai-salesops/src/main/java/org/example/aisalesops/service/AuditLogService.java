package org.example.aisalesops.service;

import org.example.aisalesops.entity.AuditLog;
import org.example.aisalesops.repository.AuditLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================
    public AuditLogService(
            AuditLogRepository auditLogRepository
    ) {

        this.auditLogRepository = auditLogRepository;
    }


    // =========================================
    // CREATE AUDIT LOG - POST
    // =========================================
    public AuditLog createAuditLog(
            AuditLog auditLog
    ) {

        return auditLogRepository.save(
                auditLog
        );
    }


    // =========================================
    // GET ALL AUDIT LOGS - GET
    // =========================================
    public List<AuditLog> getAllAuditLogs() {

        return auditLogRepository.findAll();
    }


    // =========================================
    // GET AUDIT LOG BY ID - GET
    // =========================================
    public Optional<AuditLog> getAuditLogById(
            Long id
    ) {

        return auditLogRepository.findById(
                id
        );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================
    @Transactional
    public AuditLog updateAuditLog(
            Long id,
            AuditLog updatedAuditLog
    ) {

        AuditLog existingAuditLog =
                auditLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Audit Log not found with id: " + id
                                )
                        );


        existingAuditLog.setEntityType(
                updatedAuditLog.getEntityType()
        );

        existingAuditLog.setEntityId(
                updatedAuditLog.getEntityId()
        );

        existingAuditLog.setFieldName(
                updatedAuditLog.getFieldName()
        );

        existingAuditLog.setAiValue(
                updatedAuditLog.getAiValue()
        );

        existingAuditLog.setUserValue(
                updatedAuditLog.getUserValue()
        );

        existingAuditLog.setChangedBy(
                updatedAuditLog.getChangedBy()
        );


        // changedAt is not changed during PUT

        return auditLogRepository.save(
                existingAuditLog
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================
    @Transactional
    public AuditLog partialUpdateAuditLog(
            Long id,
            AuditLog updatedAuditLog
    ) {

        AuditLog existingAuditLog =
                auditLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Audit Log not found with id: " + id
                                )
                        );


        // Update Entity Type
        if (updatedAuditLog.getEntityType() != null) {

            existingAuditLog.setEntityType(
                    updatedAuditLog.getEntityType()
            );
        }


        // Update Entity ID
        if (updatedAuditLog.getEntityId() != null) {

            existingAuditLog.setEntityId(
                    updatedAuditLog.getEntityId()
            );
        }


        // Update Field Name
        if (updatedAuditLog.getFieldName() != null) {

            existingAuditLog.setFieldName(
                    updatedAuditLog.getFieldName()
            );
        }


        // Update AI Value
        if (updatedAuditLog.getAiValue() != null) {

            existingAuditLog.setAiValue(
                    updatedAuditLog.getAiValue()
            );
        }


        // Update User Value
        if (updatedAuditLog.getUserValue() != null) {

            existingAuditLog.setUserValue(
                    updatedAuditLog.getUserValue()
            );
        }


        // Update Changed By
        if (updatedAuditLog.getChangedBy() != null) {

            existingAuditLog.setChangedBy(
                    updatedAuditLog.getChangedBy()
            );
        }


        // changedAt is intentionally not updated

        return auditLogRepository.save(
                existingAuditLog
        );
    }


    // =========================================
    // DELETE AUDIT LOG - DELETE
    // =========================================
    public void deleteAuditLog(
            Long id
    ) {

        AuditLog existingAuditLog =
                auditLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Audit Log not found with id: " + id
                                )
                        );


        auditLogRepository.delete(
                existingAuditLog
        );
    }

}