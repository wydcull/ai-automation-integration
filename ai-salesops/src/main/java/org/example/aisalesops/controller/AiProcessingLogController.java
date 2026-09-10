package org.example.aisalesops.controller;

import org.example.aisalesops.entity.AiProcessingLog;
import org.example.aisalesops.service.AiProcessingLogService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai-processing-logs")
public class AiProcessingLogController {


    private final AiProcessingLogService aiProcessingLogService;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public AiProcessingLogController(
            AiProcessingLogService aiProcessingLogService
    ) {

        this.aiProcessingLogService =
                aiProcessingLogService;
    }


    // =========================================
    // CREATE - POST
    // =========================================

    @PostMapping
    public AiProcessingLog createAiProcessingLog(
            @RequestBody AiProcessingLog aiProcessingLog
    ) {

        return aiProcessingLogService.createAiProcessingLog(
                aiProcessingLog
        );
    }


    // =========================================
    // GET ALL
    // =========================================

    @GetMapping
    public List<AiProcessingLog> getAllAiProcessingLogs() {

        return aiProcessingLogService
                .getAllAiProcessingLogs();
    }


    // =========================================
    // GET BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<AiProcessingLog>
    getAiProcessingLogById(
            @PathVariable Long id
    ) {

        Optional<AiProcessingLog> aiProcessingLog =
                aiProcessingLogService
                        .getAiProcessingLogById(id);


        return aiProcessingLog
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================

    @PutMapping("/{id}")
    public AiProcessingLog updateAiProcessingLog(
            @PathVariable Long id,
            @RequestBody AiProcessingLog updatedAiProcessingLog
    ) {

        return aiProcessingLogService
                .updateAiProcessingLog(
                        id,
                        updatedAiProcessingLog
                );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================

    @PatchMapping("/{id}")
    public AiProcessingLog partialUpdateAiProcessingLog(
            @PathVariable Long id,
            @RequestBody AiProcessingLog updatedAiProcessingLog
    ) {

        return aiProcessingLogService
                .partialUpdateAiProcessingLog(
                        id,
                        updatedAiProcessingLog
                );
    }


    // =========================================
    // DELETE
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAiProcessingLog(
            @PathVariable Long id
    ) {

        aiProcessingLogService
                .deleteAiProcessingLog(id);


        return ResponseEntity.noContent().build();
    }
}