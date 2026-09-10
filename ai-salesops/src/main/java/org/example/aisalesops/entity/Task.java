package org.example.aisalesops.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    // =========================================
    // ID
    // =========================================

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
    // TITLE
    // =========================================

    @Column(
            nullable = false,
            length = 200
    )
    private String title;


    // =========================================
    // PRIORITY
    // =========================================

    @Column(
            nullable = false,
            length = 16
    )
    private String priority = "MEDIUM";


    // =========================================
    // STATUS
    // =========================================

    @Column(
            nullable = false,
            length = 16
    )
    private String status = "OPEN";


    // =========================================
    // DUE DATE AND TIME
    // =========================================

    @Column(
            name = "due_at",
            nullable = false
    )
    private OffsetDateTime dueAt;


    // =========================================
    // ASSIGNED USER
    // =========================================

    @ManyToOne
    @JoinColumn(
            name = "assigned_user_id",
            nullable = false
    )
    private User assignedUser;


    // =========================================
    // REASON
    // =========================================

    @Column(
            columnDefinition = "TEXT"
    )
    private String reason;


    // =========================================
    // COMPLETED AT
    // =========================================

    @Column(
            name = "completed_at"
    )
    private OffsetDateTime completedAt;


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

    public Task() {
    }


    // =========================================
    // PRE PERSIST
    // =========================================

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {

            createdAt = OffsetDateTime.now();
        }

        if (priority == null) {

            priority = "MEDIUM";
        }

        if (status == null) {

            status = "OPEN";
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


    public String getTitle() {

        return title;
    }


    public void setTitle(
            String title
    ) {

        this.title = title;
    }


    public String getPriority() {

        return priority;
    }


    public void setPriority(
            String priority
    ) {

        this.priority = priority;
    }


    public String getStatus() {

        return status;
    }


    public void setStatus(
            String status
    ) {

        this.status = status;
    }


    public OffsetDateTime getDueAt() {

        return dueAt;
    }


    public void setDueAt(
            OffsetDateTime dueAt
    ) {

        this.dueAt = dueAt;
    }


    public User getAssignedUser() {

        return assignedUser;
    }


    public void setAssignedUser(
            User assignedUser
    ) {

        this.assignedUser = assignedUser;
    }


    public String getReason() {

        return reason;
    }


    public void setReason(
            String reason
    ) {

        this.reason = reason;
    }


    public OffsetDateTime getCompletedAt() {

        return completedAt;
    }


    public void setCompletedAt(
            OffsetDateTime completedAt
    ) {

        this.completedAt = completedAt;
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