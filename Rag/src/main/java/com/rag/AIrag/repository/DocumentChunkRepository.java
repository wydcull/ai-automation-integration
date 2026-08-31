package com.rag.AIrag.repository;

import com.rag.AIrag.model.DocumentChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunkEntity, UUID> {

    @Query(value = """
        SELECT id, document_id, chunk_index, content,
               1 - (embedding <=> CAST(:queryVector AS vector)) AS score
        FROM document_chunks
        WHERE (:documentId IS NULL OR document_id = CAST(:documentId AS uuid))
        ORDER BY embedding <=> CAST(:queryVector AS vector)
        LIMIT :topK
        """, nativeQuery = true)
    List<Object[]> findSimilar(
            @Param("queryVector") String queryVector,
            @Param("topK") int topK,
            @Param("documentId") String documentId  // nullable
    );

    void deleteByDocumentId(UUID documentId);
}