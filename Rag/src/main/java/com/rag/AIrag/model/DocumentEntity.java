package com.rag.AIrag.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String filename;
    private String contentType;
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    private Instant uploadedAt;
}
