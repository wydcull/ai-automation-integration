package com.rag.AIrag.repository;

import com.rag.AIrag.model.DocumentChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO document_chunks (id, document_id, chunk_index, content, embedding, created_at)
        VALUES (
          CAST(:id AS uuid),
          CAST(:documentId AS uuid),
          :chunkIndex,
          :content,
          CAST(:embedding AS vector),
          :createdAt
        )
        """, nativeQuery = true)
    void insertChunk(
            @Param("id") String id,
            @Param("documentId") String documentId,
            @Param("chunkIndex") int chunkIndex,
            @Param("content") String content,
            @Param("embedding") String embedding,
            @Param("createdAt") Instant createdAt
    );


    @Modifying
    @Transactional
    void deleteByDocumentId(UUID documentId);

    long countByDocumentId(UUID documentId);
}