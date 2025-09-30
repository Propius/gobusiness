package com.govtech.scrabble.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing feature flags for dynamic feature toggling.
 */
@Entity
@Table(name = "feature_flags")
public class FeatureFlag {
    
    @Id
    @Column(name = "flag_name", length = 100)
    private String flagName;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy = "system";
    
    // Constructors
    public FeatureFlag() {}
    
    public FeatureFlag(String flagName, Boolean enabled, String description) {
        this.flagName = flagName;
        this.enabled = enabled;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Pre-persist and pre-update hooks
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getFlagName() {
        return flagName;
    }
    
    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    @Override
    public String toString() {
        return "FeatureFlag{" +
                "flagName='" + flagName + '\'' +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                ", updatedAt=" + updatedAt +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}