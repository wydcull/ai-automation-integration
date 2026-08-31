package com.rag.AIrag.repository;

import com.rag.AIrag.model.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
}
