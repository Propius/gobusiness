package com.govtech.scrabble.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ScoreRequest {

    @NotBlank(message = "Word cannot be blank")
    @Size(max = 10, message = "Word cannot exceed 10 characters")
    private String word;

    @Positive(message = "Enhanced score must be positive")
    private Integer enhancedScore;

    private List<Integer> positions;

    private List<String> specialTiles;

    public ScoreRequest() {}

    public ScoreRequest(String word) {
        this.word = word;
    }

    public ScoreRequest(String word, Integer enhancedScore, List<Integer> positions, List<String> specialTiles) {
        this.word = word;
        this.enhancedScore = enhancedScore;
        this.positions = positions;
        this.specialTiles = specialTiles;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getEnhancedScore() {
        return enhancedScore;
    }

    public void setEnhancedScore(Integer enhancedScore) {
        this.enhancedScore = enhancedScore;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }

    public List<String> getSpecialTiles() {
        return specialTiles;
    }

    public void setSpecialTiles(List<String> specialTiles) {
        this.specialTiles = specialTiles;
    }
}