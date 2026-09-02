package com.rag.AIrag.service;

import com.rag.AIrag.dto.RetrievedChunk;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PromptBuilder {

    public String buildSystemPrompt(List<RetrievedChunk> chunks, Map<UUID, String> filenames) {
        StringBuilder ctx = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            RetrievedChunk c = chunks.get(i);
            String name = filenames.getOrDefault(c.documentId(), "unknown");
            ctx.append("[%d] %s (chunk %d):\n%s\n\n".formatted(
                    i + 1, name, c.chunkIndex(), c.content()));
        }

        return """
            You are a helpful assistant.
            Answer ONLY from the context below.
            If the answer is not in the context, say:
            "I don't have that information in the uploaded documents."
            Cite sources like [1], [2].

            Context:
            %s
            """.formatted(ctx);
    }
}