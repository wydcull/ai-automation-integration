package org.example.aisalesops.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "lead_scores")
public class LeadScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Foreign Key -> leads(id)
    @OneToOne
    @JoinColumn(
            name = "lead_id",
            nullable = false,
            unique = true
    )
    private Lead lead;


    @Column(
            name = "total_score",
            nullable = false
    )
    private Integer totalScore;


    @Column(name = "purchase_intent")
    private Integer purchaseIntent;


    @Column(name = "company_fit")
    private Integer companyFit;


    @Column(name = "budget_pts")
    private Integer budgetPts;


    @Column(name = "timeline_pts")
    private Integer timelinePts;


    @Column(name = "product_match")
    private Integer productMatch;


    @Column(name = "engagement")
    private Integer engagement;


    @Column(
            columnDefinition = "TEXT",
            nullable = false
    )
    private String explanation;


    @Column(
            name = "model_name",
            length = 64
    )
    private String modelName;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;


    // =========================================
    // DEFAULT CONSTRUCTOR
    // =========================================

    public LeadScore() {
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


    public void setId(Long id) {
        this.id = id;
    }


    public Lead getLead() {
        return lead;
    }


    public void setLead(Lead lead) {
        this.lead = lead;
    }


    public Integer getTotalScore() {
        return totalScore;
    }


    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }


    public Integer getPurchaseIntent() {
        return purchaseIntent;
    }


    public void setPurchaseIntent(Integer purchaseIntent) {
        this.purchaseIntent = purchaseIntent;
    }


    public Integer getCompanyFit() {
        return companyFit;
    }


    public void setCompanyFit(Integer companyFit) {
        this.companyFit = companyFit;
    }


    public Integer getBudgetPts() {
        return budgetPts;
    }


    public void setBudgetPts(Integer budgetPts) {
        this.budgetPts = budgetPts;
    }


    public Integer getTimelinePts() {
        return timelinePts;
    }


    public void setTimelinePts(Integer timelinePts) {
        this.timelinePts = timelinePts;
    }


    public Integer getProductMatch() {
        return productMatch;
    }


    public void setProductMatch(Integer productMatch) {
        this.productMatch = productMatch;
    }


    public Integer getEngagement() {
        return engagement;
    }


    public void setEngagement(Integer engagement) {
        this.engagement = engagement;
    }


    public String getExplanation() {
        return explanation;
    }


    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }


    public String getModelName() {
        return modelName;
    }


    public void setModelName(String modelName) {
        this.modelName = modelName;
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