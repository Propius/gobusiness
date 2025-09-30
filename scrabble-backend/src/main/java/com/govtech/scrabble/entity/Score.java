package com.govtech.scrabble.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Word cannot be blank")
    @Column(nullable = false, length = 10)
    private String word;

    @NotNull(message = "Points cannot be null")
    @Positive(message = "Points must be positive")
    @Column(nullable = false)
    private Integer points;

    @Column(name = "enhanced_score")
    private Integer enhancedScore;

    @Column(name = "positions", columnDefinition = "TEXT")
    private String positions;

    @Column(name = "special_tiles", columnDefinition = "TEXT")
    private String specialTiles;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Score() {
        this.createdAt = LocalDateTime.now();
    }

    public Score(String word, Integer points) {
        this();
        this.word = word;
        this.points = points;
        this.enhancedScore = points;
    }

    public Score(String word, Integer points, Integer enhancedScore, String positions, String specialTiles) {
        this();
        this.word = word;
        this.points = points;
        this.enhancedScore = enhancedScore;
        this.positions = positions;
        this.specialTiles = specialTiles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getEnhancedScore() {
        return enhancedScore;
    }

    public void setEnhancedScore(Integer enhancedScore) {
        this.enhancedScore = enhancedScore;
    }

    public String getPositions() {
        return positions;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }

    public String getSpecialTiles() {
        return specialTiles;
    }

    public void setSpecialTiles(String specialTiles) {
        this.specialTiles = specialTiles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}