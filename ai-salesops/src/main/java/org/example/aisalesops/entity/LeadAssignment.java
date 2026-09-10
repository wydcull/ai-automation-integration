package org.example.aisalesops.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "lead_assignments")
public class LeadAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Foreign Key -> leads(id)
    @ManyToOne
    @JoinColumn(
            name = "lead_id",
            nullable = false
    )
    private Lead lead;


    // Foreign Key -> users(id)
    @ManyToOne
    @JoinColumn(
            name = "assigned_user_id",
            nullable = false
    )
    private User assignedUser;


    // Foreign Key -> assignment_rules(id)
    @ManyToOne
    @JoinColumn(
            name = "rule_id"
    )
    private AssignmentRule assignmentRule;


    @Column(
            columnDefinition = "TEXT"
    )
    private String reason;


    @Column(
            name = "assigned_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime assignedAt;


    // =========================================
    // DEFAULT CONSTRUCTOR
    // =========================================

    public LeadAssignment() {
    }


    // =========================================
    // SET ASSIGNED TIME AUTOMATICALLY
    // =========================================

    @PrePersist
    public void prePersist() {

        if (assignedAt == null) {

            assignedAt = OffsetDateTime.now();
        }
    }


    // =========================================
    // GETTERS AND SETTERS
    // =========================================

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Lead getLead() {
        return lead;
    }


    public void setLead(Lead lead) {
        this.lead = lead;
    }


    public User getAssignedUser() {
        return assignedUser;
    }


    public void setAssignedUser(
            User assignedUser
    ) {
        this.assignedUser = assignedUser;
    }


    public AssignmentRule getAssignmentRule() {
        return assignmentRule;
    }


    public void setAssignmentRule(
            AssignmentRule assignmentRule
    ) {
        this.assignmentRule = assignmentRule;
    }


    public String getReason() {
        return reason;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }


    public OffsetDateTime getAssignedAt() {
        return assignedAt;
    }


    public void setAssignedAt(
            OffsetDateTime assignedAt
    ) {
        this.assignedAt = assignedAt;
    }
}