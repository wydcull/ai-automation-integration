package com.rag.AIrag.service;

import com.rag.AIrag.dto.AskRequest;
import com.rag.AIrag.dto.AskResponse;
import com.rag.AIrag.dto.RetrievedChunk;
import com.rag.AIrag.dto.SourceCitation;
import com.rag.AIrag.dto.groq.GroqRequest;
import com.rag.AIrag.model.DocumentEntity;
import com.rag.AIrag.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagQueryService {

    private final EmbeddingService embeddingService;
    private final VectorSearchService vectorSearchService;
    private final DocumentRepository documentRepository;
    private final PromptBuilder promptBuilder;
    private final GroqService groqService;

    @Value("${rag.top-k}")
    private int defaultTopK;

    public AskResponse ask(AskRequest request) {
        int topK = request.getTopK() != null ? request.getTopK() : defaultTopK;

        // 1. embed question
        List<Double> vector = embeddingService.embed(request.getQuestion());
        String pgVector = embeddingService.toPgVectorString(vector);

        // 2. retrieve chunks
        List<RetrievedChunk> chunks = vectorSearchService.search(
                pgVector, topK, request.getDocumentId());

        if (chunks.isEmpty()) {
            return AskResponse.builder()
                    .answer("I don't have relevant information in the uploaded documents.")
                    .sources(List.of())
                    .build();
        }

        // 3. load filenames
        Map<UUID, String> filenames = chunks.stream()
                .map(RetrievedChunk::documentId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> documentRepository.findById(id)
                                .map(DocumentEntity::getFilename)
                                .orElse("unknown")
                ));

        // 4. build prompt
        String systemPrompt = promptBuilder.buildSystemPrompt(chunks, filenames);

        // 5. call Groq
        List<GroqRequest.Message> messages = List.of(
                new GroqRequest.Message("system", systemPrompt),
                new GroqRequest.Message("user", request.getQuestion())
        );
        String answer = groqService.generateContentWithMessages(messages);

        // 6. build sources
        List<SourceCitation> sources = chunks.stream()
                .map(c -> SourceCitation.builder()
                        .documentId(c.documentId())
                        .filename(filenames.get(c.documentId()))
                        .chunkIndex(c.chunkIndex())
                        .score(c.score())
                        .build())
                .toList();

        return AskResponse.builder().answer(answer).sources(sources).build();
    }
}
