package org.example.aisalesops.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "entity_type",
            nullable = false,
            length = 40
    )
    private String entityType;


    @Column(
            name = "entity_id",
            nullable = false
    )
    private Long entityId;


    @Column(
            name = "field_name",
            nullable = false,
            length = 64
    )
    private String fieldName;


    @Column(
            name = "ai_value",
            columnDefinition = "TEXT"
    )
    private String aiValue;


    @Column(
            name = "user_value",
            columnDefinition = "TEXT"
    )
    private String userValue;


    @Column(name = "changed_by")
    private Long changedBy;


    @Column(
            name = "changed_at",
            nullable = false
    )
    private OffsetDateTime changedAt;


    // =========================================
    // DEFAULT CONSTRUCTOR
    // =========================================
    public AuditLog() {
    }


    // =========================================
    // AUTOMATICALLY SET CHANGED AT
    // =========================================
    @PrePersist
    public void prePersist() {

        if (changedAt == null) {

            changedAt = OffsetDateTime.now();
        }
    }


    // =========================================
    // GETTERS AND SETTERS
    // =========================================

    public Long getId() {

        return id;
    }


    public void setId(
            Long id
    ) {

        this.id = id;
    }


    public String getEntityType() {

        return entityType;
    }


    public void setEntityType(
            String entityType
    ) {

        this.entityType = entityType;
    }


    public Long getEntityId() {

        return entityId;
    }


    public void setEntityId(
            Long entityId
    ) {

        this.entityId = entityId;
    }


    public String getFieldName() {

        return fieldName;
    }


    public void setFieldName(
            String fieldName
    ) {

        this.fieldName = fieldName;
    }


    public String getAiValue() {

        return aiValue;
    }


    public void setAiValue(
            String aiValue
    ) {

        this.aiValue = aiValue;
    }


    public String getUserValue() {

        return userValue;
    }


    public void setUserValue(
            String userValue
    ) {

        this.userValue = userValue;
    }


    public Long getChangedBy() {

        return changedBy;
    }


    public void setChangedBy(
            Long changedBy
    ) {

        this.changedBy = changedBy;
    }


    public OffsetDateTime getChangedAt() {

        return changedAt;
    }


    public void setChangedAt(
            OffsetDateTime changedAt
    ) {

        this.changedAt = changedAt;
    }

}