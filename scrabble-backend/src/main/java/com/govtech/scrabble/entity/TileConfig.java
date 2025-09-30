package com.govtech.scrabble.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing tile configuration values for the Scrabble application.
 */
@Entity
@Table(name = "tile_configs")
public class TileConfig {
    
    @Id
    @Column(name = "config_name", length = 100)
    private String configName;
    
    @Column(name = "config_value", nullable = false)
    private Integer configValue;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy = "system";
    
    // Constructors
    public TileConfig() {}
    
    public TileConfig(String configName, Integer configValue, String description) {
        this.configName = configName;
        this.configValue = configValue;
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
    public String getConfigName() {
        return configName;
    }
    
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    
    public Integer getConfigValue() {
        return configValue;
    }
    
    public void setConfigValue(Integer configValue) {
        this.configValue = configValue;
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
        return "TileConfig{" +
                "configName='" + configName + '\'' +
                ", configValue=" + configValue +
                ", description='" + description + '\'' +
                ", updatedAt=" + updatedAt +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}