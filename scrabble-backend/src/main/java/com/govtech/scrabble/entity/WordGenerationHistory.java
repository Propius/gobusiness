package com.govtech.scrabble.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

/**
 * Entity representing the history of word generation requests.
 * Tracks usage patterns, performance metrics, and generation statistics for word finder operations.
 */
@Entity
@Table(name = "word_generation_history", indexes = {
    @Index(name = "idx_generation_mode", columnList = "generation_mode"),
    @Index(name = "idx_generated_at", columnList = "generated_at"),
    @Index(name = "idx_user_session", columnList = "user_session")
})
public class WordGenerationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Input letters cannot be blank")
    @Column(name = "input_letters", length = 255, nullable = false)
    private String inputLetters;

    @NotBlank(message = "Generation mode cannot be blank")
    @Pattern(regexp = "sampling|exhaustive", message = "Generation mode must be 'sampling' or 'exhaustive'")
    @Column(name = "generation_mode", length = 20, nullable = false)
    private String generationMode;

    @NotNull(message = "Results count cannot be null")
    @PositiveOrZero(message = "Results count must be zero or positive")
    @Column(name = "results_count")
    private Integer resultsCount;

    @NotNull(message = "Execution time cannot be null")
    @PositiveOrZero(message = "Execution time must be zero or positive")
    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "user_session", length = 255)
    private String userSession;

    /**
     * Default constructor initializing timestamps and default values.
     */
    public WordGenerationHistory() {
        this.generatedAt = LocalDateTime.now();
        this.resultsCount = 0;
        this.executionTimeMs = 0;
    }

    /**
     * Constructor with required fields.
     *
     * @param inputLetters    The letters used for word generation
     * @param generationMode  The mode used (sampling or exhaustive)
     * @param resultsCount    Number of words generated
     * @param executionTimeMs Execution time in milliseconds
     */
    public WordGenerationHistory(String inputLetters, String generationMode,
                                 Integer resultsCount, Integer executionTimeMs) {
        this();
        this.inputLetters = inputLetters;
        this.generationMode = generationMode;
        this.resultsCount = resultsCount;
        this.executionTimeMs = executionTimeMs;
    }

    /**
     * Constructor with all fields including user session.
     *
     * @param inputLetters    The letters used for word generation
     * @param generationMode  The mode used (sampling or exhaustive)
     * @param resultsCount    Number of words generated
     * @param executionTimeMs Execution time in milliseconds
     * @param userSession     The user session identifier
     */
    public WordGenerationHistory(String inputLetters, String generationMode,
                                 Integer resultsCount, Integer executionTimeMs,
                                 String userSession) {
        this(inputLetters, generationMode, resultsCount, executionTimeMs);
        this.userSession = userSession;
    }

    /**
     * Calculates average execution time per word generated.
     *
     * @return Average time in milliseconds per word, or 0 if no words generated
     */
    public double getAverageTimePerWord() {
        if (resultsCount == null || resultsCount == 0 || executionTimeMs == null) {
            return 0.0;
        }
        return (double) executionTimeMs / resultsCount;
    }

    /**
     * Checks if the generation was successful (produced results).
     *
     * @return true if at least one word was generated
     */
    public boolean wasSuccessful() {
        return resultsCount != null && resultsCount > 0;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInputLetters() {
        return inputLetters;
    }

    public void setInputLetters(String inputLetters) {
        this.inputLetters = inputLetters;
    }

    public String getGenerationMode() {
        return generationMode;
    }

    public void setGenerationMode(String generationMode) {
        this.generationMode = generationMode;
    }

    public Integer getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(Integer resultsCount) {
        this.resultsCount = resultsCount;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }

    @Override
    public String toString() {
        return "WordGenerationHistory{" +
                "id=" + id +
                ", inputLetters='" + inputLetters + '\'' +
                ", generationMode='" + generationMode + '\'' +
                ", resultsCount=" + resultsCount +
                ", executionTimeMs=" + executionTimeMs +
                ", generatedAt=" + generatedAt +
                ", userSession='" + userSession + '\'' +
                '}';
    }
}