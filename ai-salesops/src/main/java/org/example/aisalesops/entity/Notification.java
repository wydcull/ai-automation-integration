package org.example.aisalesops.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;


    @Column(
            nullable = false,
            length = 64
    )
    private String type;


    @Column(
            nullable = false,
            length = 200
    )
    private String title;


    @Column(
            columnDefinition = "TEXT"
    )
    private String body;


    @Column(
            name = "lead_id"
    )
    private Long leadId;


    @Column(
            name = "read_at"
    )
    private OffsetDateTime readAt;


    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;


    // =========================================
    // DEFAULT CONSTRUCTOR
    // =========================================
    public Notification() {
    }


    // =========================================
    // AUTOMATICALLY SET CREATED AT
    // =========================================
    @PrePersist
    public void prePersist() {

        if (createdAt == null) {

            createdAt = OffsetDateTime.now();
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


    public Long getUserId() {

        return userId;
    }


    public void setUserId(
            Long userId
    ) {

        this.userId = userId;
    }


    public String getType() {

        return type;
    }


    public void setType(
            String type
    ) {

        this.type = type;
    }


    public String getTitle() {

        return title;
    }


    public void setTitle(
            String title
    ) {

        this.title = title;
    }


    public String getBody() {

        return body;
    }


    public void setBody(
            String body
    ) {

        this.body = body;
    }


    public Long getLeadId() {

        return leadId;
    }


    public void setLeadId(
            Long leadId
    ) {

        this.leadId = leadId;
    }


    public OffsetDateTime getReadAt() {

        return readAt;
    }


    public void setReadAt(
            OffsetDateTime readAt
    ) {

        this.readAt = readAt;
    }


    public OffsetDateTime getCreatedAt() {

        return createdAt;
    }


    public void setCreatedAt(
            OffsetDateTime createdAt
    ) {

        this.createdAt = createdAt;
    }

}