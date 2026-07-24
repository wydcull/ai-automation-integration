package com.firstaiAutomationSystem.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_accounts")
public class EmailAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName;

    @Column(unique = true, nullable = false)
    private String emailAddress;

    private String gmailCredentialsPath;
    private String gmailTokensDirectory;
    private Boolean enabled = true;
    private Boolean autoReplyEnabled = false;
    private String autoReplyCategories; // Comma-separated

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    // Getters and setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getGmailCredentialsPath() {
        return gmailCredentialsPath;
    }

    public void setGmailCredentialsPath(String gmailCredentialsPath) {
        this.gmailCredentialsPath = gmailCredentialsPath;
    }

    public String getGmailTokensDirectory() {
        return gmailTokensDirectory;
    }

    public void setGmailTokensDirectory(String gmailTokensDirectory) {
        this.gmailTokensDirectory = gmailTokensDirectory;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAutoReplyEnabled() {
        return autoReplyEnabled;
    }

    public void setAutoReplyEnabled(Boolean autoReplyEnabled) {
        this.autoReplyEnabled = autoReplyEnabled;
    }

    public String getAutoReplyCategories() {
        return autoReplyCategories;
    }

    public void setAutoReplyCategories(String autoReplyCategories) {
        this.autoReplyCategories = autoReplyCategories;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}