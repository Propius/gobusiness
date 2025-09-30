package com.govtech.scrabble.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing cached dictionary validation results.
 * Stores word validation results to improve performance by reducing external dictionary API calls.
 */
@Entity
@Table(name = "dictionary_cache", indexes = {
    @Index(name = "idx_dictionary_cache_source", columnList = "dictionary_source"),
    @Index(name = "idx_dictionary_cache_accessed", columnList = "last_accessed")
})
public class DictionaryCache {

    @Id
    @NotBlank(message = "Word cannot be blank")
    @Column(length = 255, nullable = false)
    private String word;

    @NotNull(message = "Validity status cannot be null")
    @Column(name = "is_valid", nullable = false)
    private Boolean isValid;

    @Column(name = "dictionary_source", length = 50)
    private String dictionarySource;

    @Column(name = "cached_at", nullable = false)
    private LocalDateTime cachedAt;

    @Column(name = "access_count")
    private Integer accessCount;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    /**
     * Default constructor initializing timestamps and default values.
     */
    public DictionaryCache() {
        this.cachedAt = LocalDateTime.now();
        this.lastAccessed = LocalDateTime.now();
        this.accessCount = 1;
        this.dictionarySource = "LANGUAGETOOL";
    }

    /**
     * Constructor with required fields.
     *
     * @param word     The word to cache
     * @param isValid  Whether the word is valid according to the dictionary
     */
    public DictionaryCache(String word, Boolean isValid) {
        this();
        this.word = word;
        this.isValid = isValid;
    }

    /**
     * Constructor with all fields.
     *
     * @param word              The word to cache
     * @param isValid           Whether the word is valid according to the dictionary
     * @param dictionarySource  The dictionary source used for validation
     */
    public DictionaryCache(String word, Boolean isValid, String dictionarySource) {
        this(word, isValid);
        this.dictionarySource = dictionarySource;
    }

    /**
     * Updates access tracking when the cached entry is accessed.
     */
    public void recordAccess() {
        this.accessCount++;
        this.lastAccessed = LocalDateTime.now();
    }

    // Getters and Setters

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public String getDictionarySource() {
        return dictionarySource;
    }

    public void setDictionarySource(String dictionarySource) {
        this.dictionarySource = dictionarySource;
    }

    public LocalDateTime getCachedAt() {
        return cachedAt;
    }

    public void setCachedAt(LocalDateTime cachedAt) {
        this.cachedAt = cachedAt;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    @Override
    public String toString() {
        return "DictionaryCache{" +
                "word='" + word + '\'' +
                ", isValid=" + isValid +
                ", dictionarySource='" + dictionarySource + '\'' +
                ", cachedAt=" + cachedAt +
                ", accessCount=" + accessCount +
                ", lastAccessed=" + lastAccessed +
                '}';
    }
}