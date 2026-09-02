package com.rag.AIrag.controller;

import com.rag.AIrag.dto.DocumentSummary;
import com.rag.AIrag.dto.IngestResponse;
import com.rag.AIrag.model.DocumentEntity;
import com.rag.AIrag.repository.DocumentChunkRepository;
import com.rag.AIrag.repository.DocumentRepository;
import com.rag.AIrag.service.DocumentIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentIngestionService ingestionService;
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;

    /**
     * Upload .txt, .md, or .pdf → extract text → chunk → embed → store
     * POST /api/documents
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !isAllowedFileType(filename)) {
            return ResponseEntity.badRequest()
                    .body("Unsupported file type. Allowed: .txt, .md, .pdf");
        }

        try {
            log.info("Upload request received for file: {}", filename);
            IngestResponse response = ingestionService.ingest(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid upload request for {}: {}", filename, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (IOException e) {
            log.error("IO error while ingesting document: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to read uploaded file");

        } catch (Exception e) {
            log.error("Failed to ingest document: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Document ingestion failed: " + e.getMessage());
        }
    }

    /**
     * List all uploaded documents with chunk counts
     * GET /api/documents
     */
    @GetMapping
    public List<DocumentSummary> list() {
        return documentRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * Get one document by id
     * GET /api/documents/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentSummary> getById(@PathVariable UUID id) {
        return documentRepository.findById(id)
                .map(doc -> ResponseEntity.ok(toSummary(doc)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete document and all its chunks
     * DELETE /api/documents/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!documentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        chunkRepository.deleteByDocumentId(id);
        documentRepository.deleteById(id);
        log.info("Deleted document: {}", id);
        return ResponseEntity.noContent().build();
    }

    private boolean isAllowedFileType(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".txt")
                || lower.endsWith(".md")
                || lower.endsWith(".pdf");
    }

    private DocumentSummary toSummary(DocumentEntity doc) {
        return DocumentSummary.builder()
                .id(doc.getId())
                .filename(doc.getFilename())
                .status(doc.getStatus())
                .uploadedAt(doc.getUploadedAt())
                .chunkCount(chunkRepository.countByDocumentId(doc.getId()))
                .build();
    }
}