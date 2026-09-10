package org.example.aisalesops.service;

import org.example.aisalesops.entity.AiProcessingLog;
import org.example.aisalesops.repository.AiProcessingLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AiProcessingLogService {


    private final AiProcessingLogRepository aiProcessingLogRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public AiProcessingLogService(
            AiProcessingLogRepository aiProcessingLogRepository
    ) {

        this.aiProcessingLogRepository =
                aiProcessingLogRepository;
    }


    // =========================================
    // CREATE - POST
    // =========================================

    public AiProcessingLog createAiProcessingLog(
            AiProcessingLog aiProcessingLog
    ) {

        return aiProcessingLogRepository.save(
                aiProcessingLog
        );
    }


    // =========================================
    // GET ALL
    // =========================================

    public List<AiProcessingLog> getAllAiProcessingLogs() {

        return aiProcessingLogRepository.findAll();
    }


    // =========================================
    // GET BY ID
    // =========================================

    public Optional<AiProcessingLog> getAiProcessingLogById(
            Long id
    ) {

        return aiProcessingLogRepository.findById(id);
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @Transactional
    public AiProcessingLog updateAiProcessingLog(
            Long id,
            AiProcessingLog updatedAiProcessingLog
    ) {

        AiProcessingLog existingAiProcessingLog =
                aiProcessingLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Processing Log not found with id: "
                                                + id
                                )
                        );


        existingAiProcessingLog.setRequestId(
                updatedAiProcessingLog.getRequestId()
        );

        existingAiProcessingLog.setLeadId(
                updatedAiProcessingLog.getLeadId()
        );

        existingAiProcessingLog.setEmailMessageId(
                updatedAiProcessingLog.getEmailMessageId()
        );

        existingAiProcessingLog.setOperation(
                updatedAiProcessingLog.getOperation()
        );

        existingAiProcessingLog.setModelName(
                updatedAiProcessingLog.getModelName()
        );

        existingAiProcessingLog.setInputRef(
                updatedAiProcessingLog.getInputRef()
        );

        existingAiProcessingLog.setOutputJson(
                updatedAiProcessingLog.getOutputJson()
        );

        existingAiProcessingLog.setConfidence(
                updatedAiProcessingLog.getConfidence()
        );

        existingAiProcessingLog.setStatus(
                updatedAiProcessingLog.getStatus()
        );

        existingAiProcessingLog.setErrorMessage(
                updatedAiProcessingLog.getErrorMessage()
        );

        existingAiProcessingLog.setProcessingMs(
                updatedAiProcessingLog.getProcessingMs()
        );


        // createdAt is intentionally not updated

        return aiProcessingLogRepository.save(
                existingAiProcessingLog
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @Transactional
    public AiProcessingLog partialUpdateAiProcessingLog(
            Long id,
            AiProcessingLog updatedAiProcessingLog
    ) {

        AiProcessingLog existingAiProcessingLog =
                aiProcessingLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Processing Log not found with id: "
                                                + id
                                )
                        );


        // Request ID
        if (updatedAiProcessingLog.getRequestId() != null) {

            existingAiProcessingLog.setRequestId(
                    updatedAiProcessingLog.getRequestId()
            );
        }


        // Lead ID
        if (updatedAiProcessingLog.getLeadId() != null) {

            existingAiProcessingLog.setLeadId(
                    updatedAiProcessingLog.getLeadId()
            );
        }


        // Email Message ID
        if (updatedAiProcessingLog.getEmailMessageId() != null) {

            existingAiProcessingLog.setEmailMessageId(
                    updatedAiProcessingLog.getEmailMessageId()
            );
        }


        // Operation
        if (updatedAiProcessingLog.getOperation() != null) {

            existingAiProcessingLog.setOperation(
                    updatedAiProcessingLog.getOperation()
            );
        }


        // Model Name
        if (updatedAiProcessingLog.getModelName() != null) {

            existingAiProcessingLog.setModelName(
                    updatedAiProcessingLog.getModelName()
            );
        }


        // Input Reference
        if (updatedAiProcessingLog.getInputRef() != null) {

            existingAiProcessingLog.setInputRef(
                    updatedAiProcessingLog.getInputRef()
            );
        }


        // Output JSON
        if (updatedAiProcessingLog.getOutputJson() != null) {

            existingAiProcessingLog.setOutputJson(
                    updatedAiProcessingLog.getOutputJson()
            );
        }


        // Confidence
        if (updatedAiProcessingLog.getConfidence() != null) {

            existingAiProcessingLog.setConfidence(
                    updatedAiProcessingLog.getConfidence()
            );
        }


        // Status
        if (updatedAiProcessingLog.getStatus() != null) {

            existingAiProcessingLog.setStatus(
                    updatedAiProcessingLog.getStatus()
            );
        }


        // Error Message
        if (updatedAiProcessingLog.getErrorMessage() != null) {

            existingAiProcessingLog.setErrorMessage(
                    updatedAiProcessingLog.getErrorMessage()
            );
        }


        // Processing Time
        if (updatedAiProcessingLog.getProcessingMs() != null) {

            existingAiProcessingLog.setProcessingMs(
                    updatedAiProcessingLog.getProcessingMs()
            );
        }


        // createdAt is intentionally not updated

        return aiProcessingLogRepository.save(
                existingAiProcessingLog
        );
    }


    // =========================================
    // DELETE
    // =========================================

    public void deleteAiProcessingLog(
            Long id
    ) {

        AiProcessingLog existingAiProcessingLog =
                aiProcessingLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI Processing Log not found with id: "
                                                + id
                                )
                        );


        aiProcessingLogRepository.delete(
                existingAiProcessingLog
        );
    }
}