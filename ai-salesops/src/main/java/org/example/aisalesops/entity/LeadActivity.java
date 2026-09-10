package org.example.aisalesops.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "lead_activities")
public class LeadActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================
    // LEAD
    // =========================================

    @ManyToOne
    @JoinColumn(
            name = "lead_id",
            nullable = false
    )
    private Lead lead;


    // =========================================
    // USER
    // =========================================

    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private User user;


    // =========================================
    // ACTIVITY TYPE
    // =========================================

    @Column(
            name = "activity_type",
            nullable = false,
            length = 40
    )
    private String activityType;


    // =========================================
    // CONTENT
    // =========================================

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;


    // =========================================
    // CREATED AT
    // =========================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;


    // =========================================
    // DEFAULT CONSTRUCTOR
    // =========================================

    public LeadActivity() {
    }


    // =========================================
    // PRE PERSIST
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


    public Lead getLead() {

        return lead;
    }


    public void setLead(
            Lead lead
    ) {

        this.lead = lead;
    }


    public User getUser() {

        return user;
    }


    public void setUser(
            User user
    ) {

        this.user = user;
    }


    public String getActivityType() {

        return activityType;
    }


    public void setActivityType(
            String activityType
    ) {

        this.activityType = activityType;
    }


    public String getContent() {

        return content;
    }


    public void setContent(
            String content
    ) {

        this.content = content;
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