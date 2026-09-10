package org.example.aisalesops.service;

import org.example.aisalesops.entity.ExtractionFieldConfidence;
import org.example.aisalesops.repository.ExtractionFieldConfidenceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExtractionFieldConfidenceService {


    private final ExtractionFieldConfidenceRepository
            extractionFieldConfidenceRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public ExtractionFieldConfidenceService(
            ExtractionFieldConfidenceRepository
                    extractionFieldConfidenceRepository
    ) {

        this.extractionFieldConfidenceRepository =
                extractionFieldConfidenceRepository;
    }


    // =========================================
    // CREATE - POST
    // =========================================

    public ExtractionFieldConfidence
    createExtractionFieldConfidence(
            ExtractionFieldConfidence extractionFieldConfidence
    ) {

        return extractionFieldConfidenceRepository.save(
                extractionFieldConfidence
        );
    }


    // =========================================
    // GET ALL
    // =========================================

    public List<ExtractionFieldConfidence>
    getAllExtractionFieldConfidences() {

        return extractionFieldConfidenceRepository.findAll();
    }


    // =========================================
    // GET BY ID
    // =========================================

    public Optional<ExtractionFieldConfidence>
    getExtractionFieldConfidenceById(
            Long id
    ) {

        return extractionFieldConfidenceRepository.findById(
                id
        );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @Transactional
    public ExtractionFieldConfidence
    updateExtractionFieldConfidence(
            Long id,
            ExtractionFieldConfidence
                    updatedExtractionFieldConfidence
    ) {

        ExtractionFieldConfidence
                existingExtractionFieldConfidence =
                extractionFieldConfidenceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Extraction Field Confidence "
                                                + "not found with id: "
                                                + id
                                )
                        );


        existingExtractionFieldConfidence.setLeadId(
                updatedExtractionFieldConfidence
                        .getLeadId()
        );


        existingExtractionFieldConfidence.setFieldName(
                updatedExtractionFieldConfidence
                        .getFieldName()
        );


        existingExtractionFieldConfidence.setConfidence(
                updatedExtractionFieldConfidence
                        .getConfidence()
        );


        existingExtractionFieldConfidence.setFlagged(
                updatedExtractionFieldConfidence
                        .getFlagged()
        );


        existingExtractionFieldConfidence.setAiValue(
                updatedExtractionFieldConfidence
                        .getAiValue()
        );


        return extractionFieldConfidenceRepository.save(
                existingExtractionFieldConfidence
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @Transactional
    public ExtractionFieldConfidence
    partialUpdateExtractionFieldConfidence(
            Long id,
            ExtractionFieldConfidence
                    updatedExtractionFieldConfidence
    ) {

        ExtractionFieldConfidence
                existingExtractionFieldConfidence =
                extractionFieldConfidenceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Extraction Field Confidence "
                                                + "not found with id: "
                                                + id
                                )
                        );


        // Update Lead ID
        if (updatedExtractionFieldConfidence
                .getLeadId() != null) {

            existingExtractionFieldConfidence.setLeadId(
                    updatedExtractionFieldConfidence
                            .getLeadId()
            );
        }


        // Update Field Name
        if (updatedExtractionFieldConfidence
                .getFieldName() != null) {

            existingExtractionFieldConfidence.setFieldName(
                    updatedExtractionFieldConfidence
                            .getFieldName()
            );
        }


        // Update Confidence
        if (updatedExtractionFieldConfidence
                .getConfidence() != null) {

            existingExtractionFieldConfidence.setConfidence(
                    updatedExtractionFieldConfidence
                            .getConfidence()
            );
        }


        // Update Flagged
        if (updatedExtractionFieldConfidence
                .getFlagged() != null) {

            existingExtractionFieldConfidence.setFlagged(
                    updatedExtractionFieldConfidence
                            .getFlagged()
            );
        }


        // Update AI Value
        if (updatedExtractionFieldConfidence
                .getAiValue() != null) {

            existingExtractionFieldConfidence.setAiValue(
                    updatedExtractionFieldConfidence
                            .getAiValue()
            );
        }


        return extractionFieldConfidenceRepository.save(
                existingExtractionFieldConfidence
        );
    }


    // =========================================
    // DELETE
    // =========================================

    public void deleteExtractionFieldConfidence(
            Long id
    ) {

        ExtractionFieldConfidence
                existingExtractionFieldConfidence =
                extractionFieldConfidenceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Extraction Field Confidence "
                                                + "not found with id: "
                                                + id
                                )
                        );


        extractionFieldConfidenceRepository.delete(
                existingExtractionFieldConfidence
        );
    }
}