package com.govtech.scrabble.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response containing calculated score with optional special tile information")
public class CalculateScoreResponse {
    
    @Schema(description = "The word that was scored", example = "HELLO")
    private String word;
    
    @Schema(description = "Base score without special tiles", example = "8")
    private Integer baseScore;
    
    @Schema(description = "Total score including special tile bonuses", example = "16")
    private Integer totalScore;
    
    @Schema(description = "Whether the word is valid in the dictionary", example = "true")
    private Boolean isValidWord;
    
    @Schema(description = "Validation message if word is invalid", example = "Word not found in dictionary")
    private String validationMessage;
    
    @Schema(description = "List of special tile bonuses applied", example = "[\"Double Letter at position 1\", \"Triple Word at position 3\"]")
    private List<String> specialTileBonuses;
    
    public CalculateScoreResponse() {}
    
    public CalculateScoreResponse(String word, Integer totalScore) {
        this.word = word;
        this.baseScore = totalScore;
        this.totalScore = totalScore;
        this.isValidWord = true;
    }
    
    public CalculateScoreResponse(String word, Integer totalScore, Boolean isValidWord, String validationMessage) {
        this.word = word;
        this.baseScore = totalScore;
        this.totalScore = totalScore;
        this.isValidWord = isValidWord;
        this.validationMessage = validationMessage;
    }
    
    public CalculateScoreResponse(String word, Integer baseScore, Integer totalScore, Boolean isValidWord, 
                                String validationMessage, List<String> specialTileBonuses) {
        this.word = word;
        this.baseScore = baseScore;
        this.totalScore = totalScore;
        this.isValidWord = isValidWord;
        this.validationMessage = validationMessage;
        this.specialTileBonuses = specialTileBonuses;
    }
    
    public String getWord() {
        return word;
    }
    
    public void setWord(String word) {
        this.word = word;
    }
    
    public Integer getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
    
    public Boolean getIsValidWord() {
        return isValidWord;
    }
    
    public void setIsValidWord(Boolean isValidWord) {
        this.isValidWord = isValidWord;
    }
    
    public String getValidationMessage() {
        return validationMessage;
    }
    
    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
    
    public Integer getBaseScore() {
        return baseScore;
    }
    
    public void setBaseScore(Integer baseScore) {
        this.baseScore = baseScore;
    }
    
    public List<String> getSpecialTileBonuses() {
        return specialTileBonuses;
    }
    
    public void setSpecialTileBonuses(List<String> specialTileBonuses) {
        this.specialTileBonuses = specialTileBonuses;
    }
}