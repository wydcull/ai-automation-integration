package org.example.aisalesops.controller;

import org.example.aisalesops.entity.ExtractionFieldConfidence;
import org.example.aisalesops.service.ExtractionFieldConfidenceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/extraction-field-confidences")
public class ExtractionFieldConfidenceController {


    private final ExtractionFieldConfidenceService
            extractionFieldConfidenceService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public ExtractionFieldConfidenceController(
            ExtractionFieldConfidenceService
                    extractionFieldConfidenceService
    ) {

        this.extractionFieldConfidenceService =
                extractionFieldConfidenceService;
    }


    // =========================================
    // CREATE - POST
    // =========================================

    @PostMapping
    public ExtractionFieldConfidence
    createExtractionFieldConfidence(
            @RequestBody
            ExtractionFieldConfidence
                    extractionFieldConfidence
    ) {

        return extractionFieldConfidenceService
                .createExtractionFieldConfidence(
                        extractionFieldConfidence
                );
    }


    // =========================================
    // GET ALL
    // =========================================

    @GetMapping
    public List<ExtractionFieldConfidence>
    getAllExtractionFieldConfidences() {

        return extractionFieldConfidenceService
                .getAllExtractionFieldConfidences();
    }


    // =========================================
    // GET BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<ExtractionFieldConfidence>
    getExtractionFieldConfidenceById(
            @PathVariable Long id
    ) {

        Optional<ExtractionFieldConfidence>
                extractionFieldConfidence =
                extractionFieldConfidenceService
                        .getExtractionFieldConfidenceById(
                                id
                        );


        return extractionFieldConfidence
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @PutMapping("/{id}")
    public ExtractionFieldConfidence
    updateExtractionFieldConfidence(
            @PathVariable Long id,
            @RequestBody
            ExtractionFieldConfidence
                    updatedExtractionFieldConfidence
    ) {

        return extractionFieldConfidenceService
                .updateExtractionFieldConfidence(
                        id,
                        updatedExtractionFieldConfidence
                );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @PatchMapping("/{id}")
    public ExtractionFieldConfidence
    partialUpdateExtractionFieldConfidence(
            @PathVariable Long id,
            @RequestBody
            ExtractionFieldConfidence
                    updatedExtractionFieldConfidence
    ) {

        return extractionFieldConfidenceService
                .partialUpdateExtractionFieldConfidence(
                        id,
                        updatedExtractionFieldConfidence
                );
    }


    // =========================================
    // DELETE
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteExtractionFieldConfidence(
            @PathVariable Long id
    ) {

        extractionFieldConfidenceService
                .deleteExtractionFieldConfidence(
                        id
                );


        return ResponseEntity
                .noContent()
                .build();
    }
}