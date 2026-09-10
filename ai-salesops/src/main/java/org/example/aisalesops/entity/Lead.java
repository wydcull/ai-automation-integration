package org.example.aisalesops.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================
    // LEAD CODE
    // =========================================

    @Column(
            name = "lead_code",
            nullable = false,
            unique = true,
            length = 32
    )
    private String leadCode;


    // =========================================
    // CUSTOMER
    // =========================================

    @ManyToOne
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private Customer customer;


    // =========================================
    // PRODUCT DETAILS
    // =========================================

    @Column(
            name = "product_name",
            length = 120
    )
    private String productName;


    @Column(
            name = "product_model",
            length = 64
    )
    private String productModel;


    @Column(
            precision = 14,
            scale = 2
    )
    private BigDecimal quantity;


    @Column(length = 32)
    private String unit;


    @Column(columnDefinition = "TEXT")
    private String specifications;


    @Column(
            precision = 14,
            scale = 2
    )
    private BigDecimal budget;


    @Column(name = "delivery_days")
    private Integer deliveryDays;


    // =========================================
    // LEAD INFORMATION
    // =========================================

    @Column(
            nullable = false,
            length = 40
    )
    private String intent;


    @Column(length = 16)
    private String urgency;


    @Column(
            nullable = false,
            length = 40
    )
    private String status = "NEW";


    @Column
    private Integer score;


    @Column(
            name = "score_band",
            length = 16
    )
    private String scoreBand;


    @Column(
            nullable = false,
            length = 32
    )
    private String source = "EMAIL";


    // =========================================
    // ASSIGNED USER
    // =========================================

    @ManyToOne
    @JoinColumn(
            name = "assigned_user_id"
    )
    private User assignedUser;


    // =========================================
    // EMAIL MESSAGE
    // =========================================

    @ManyToOne
    @JoinColumn(
            name = "email_message_id"
    )
    private EmailMessage emailMessage;


    // =========================================
    // AI DETAILS
    // =========================================

    @Column(
            name = "ai_summary",
            columnDefinition = "TEXT"
    )
    private String aiSummary;


    @Column(
            name = "recommended_action",
            columnDefinition = "TEXT"
    )
    private String recommendedAction;


    // =========================================
    // TIMESTAMPS
    // =========================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;


    @Column(
            name = "updated_at",
            nullable = false
    )
    private OffsetDateTime updatedAt;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public Lead() {
    }


    // =========================================
    // BEFORE INSERT
    // =========================================

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }

        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }

        if (status == null) {
            status = "NEW";
        }

        if (source == null) {
            source = "EMAIL";
        }
    }


    // =========================================
    // BEFORE UPDATE
    // =========================================

    @PreUpdate
    public void preUpdate() {

        updatedAt = OffsetDateTime.now();
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


    public String getLeadCode() {
        return leadCode;
    }

    public void setLeadCode(String leadCode) {
        this.leadCode = leadCode;
    }


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }


    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }


    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }


    public Integer getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(Integer deliveryDays) {
        this.deliveryDays = deliveryDays;
    }


    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }


    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }


    public String getScoreBand() {
        return scoreBand;
    }

    public void setScoreBand(String scoreBand) {
        this.scoreBand = scoreBand;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }


    public EmailMessage getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }


    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }


    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(
            String recommendedAction
    ) {
        this.recommendedAction = recommendedAction;
    }


    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            OffsetDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }


    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            OffsetDateTime updatedAt
    ) {
        this.updatedAt = updatedAt;
    }

}