package org.example.aisalesops.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "extraction_field_confidence",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "lead_id",
                                "field_name"
                        }
                )
        }
)
public class ExtractionFieldConfidence {

    // =========================================
    // PRIMARY KEY
    // =========================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    // =========================================
    // LEAD ID
    // FK → leads(id)
    // =========================================

    @Column(
            name = "lead_id",
            nullable = false
    )
    private Long leadId;


    // =========================================
    // FIELD NAME
    // =========================================

    @Column(
            name = "field_name",
            nullable = false,
            length = 64
    )
    private String fieldName;


    // =========================================
    // AI CONFIDENCE SCORE
    // NUMERIC(5,2)
    // =========================================

    @Column(
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal confidence;


    // =========================================
    // FLAGGED
    // DEFAULT FALSE
    // =========================================

    @Column(
            nullable = false
    )
    private Boolean flagged = false;


    // =========================================
    // AI EXTRACTED VALUE
    // =========================================

    @Column(
            name = "ai_value",
            columnDefinition = "TEXT"
    )
    private String aiValue;


    // =========================================
    // DEFAULT CONSTRUCTOR
    // =========================================

    public ExtractionFieldConfidence() {

    }


    // =========================================
    // PRE PERSIST
    // =========================================

    @PrePersist
    public void prePersist() {

        if (flagged == null) {

            flagged = false;
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


    public Long getLeadId() {

        return leadId;
    }


    public void setLeadId(
            Long leadId
    ) {

        this.leadId = leadId;
    }


    public String getFieldName() {

        return fieldName;
    }


    public void setFieldName(
            String fieldName
    ) {

        this.fieldName = fieldName;
    }


    public BigDecimal getConfidence() {

        return confidence;
    }


    public void setConfidence(
            BigDecimal confidence
    ) {

        this.confidence = confidence;
    }


    public Boolean getFlagged() {

        return flagged;
    }


    public void setFlagged(
            Boolean flagged
    ) {

        this.flagged = flagged;
    }


    public String getAiValue() {

        return aiValue;
    }


    public void setAiValue(
            String aiValue
    ) {

        this.aiValue = aiValue;
    }
}