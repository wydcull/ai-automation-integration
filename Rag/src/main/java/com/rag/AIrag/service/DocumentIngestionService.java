package com.rag.AIrag.service;

import com.rag.AIrag.dto.IngestResponse;
import com.rag.AIrag.model.DocumentEntity;
import com.rag.AIrag.model.DocumentStatus;
import com.rag.AIrag.repository.DocumentChunkRepository;
import com.rag.AIrag.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionService {

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".txt", ".md", ".pdf");

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final TextChunker textChunker;
    private final EmbeddingService embeddingService;
    private final DocumentParser documentParser;

    public IngestResponse ingest(MultipartFile file) throws IOException {
        validateFile(file);

        DocumentEntity doc = DocumentEntity.builder()
                .filename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .status(DocumentStatus.PENDING)
                .uploadedAt(Instant.now())
                .build();
        doc = documentRepository.save(doc);

        try {
            log.info("Extracting text from: {}", doc.getFilename());
            String text = documentParser.extractText(file);

            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException("File contains no readable text");
            }

            List<String> chunks = textChunker.chunk(text);
            // Prefer sentence-level chunks for short docs
            if (chunks.size() == 1 && text.length() < 500) {
                chunks = List.of(text.split("(?<=[.!?])\\s+"));
                chunks = chunks.stream().map(String::trim).filter(s -> !s.isBlank()).toList();
            }
            log.info("Created {} chunks for document: {}", chunks.size(), doc.getFilename());

            List<List<Double>> embeddings = embeddingService.embedBatch(chunks);

            for (int i = 0; i < chunks.size(); i++) {
                String pgVector = embeddingService.toPgVectorString(embeddings.get(i));

                chunkRepository.insertChunk(
                        UUID.randomUUID().toString(),
                        doc.getId().toString(),
                        i,
                        chunks.get(i),
                        pgVector,
                        Instant.now()
                );
            }

            doc.setStatus(DocumentStatus.INDEXED);
            documentRepository.save(doc);

            log.info("Successfully indexed document: {} ({} chunks)", doc.getFilename(), chunks.size());

            return IngestResponse.builder()
                    .documentId(doc.getId())
                    .filename(doc.getFilename())
                    .chunkCount(chunks.size())
                    .status(DocumentStatus.INDEXED)
                    .build();

        } catch (Exception e) {
            log.error("Ingestion failed for {}", doc.getFilename(), e);
            doc.setStatus(DocumentStatus.FAILED);
            documentRepository.save(doc);
            throw e;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename is missing");
        }

        String lower = filename.toLowerCase();
        boolean allowed = ALLOWED_EXTENSIONS.stream().anyMatch(lower::endsWith);
        if (!allowed) {
            throw new IllegalArgumentException(
                    "Unsupported file type. Allowed: .txt, .md, .pdf");
        }
    }
}