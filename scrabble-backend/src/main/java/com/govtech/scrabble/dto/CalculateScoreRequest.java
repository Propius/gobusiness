package com.govtech.scrabble.dto;

import com.govtech.scrabble.validation.ValidWord;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request for calculating score with optional special tiles")
public class CalculateScoreRequest {
    
    @NotBlank(message = "Word cannot be blank")
    @Size(min = 1, max = 15, message = "Word must be between 1 and 15 characters")
    @ValidWord
    @Schema(description = "Word to calculate score for", example = "HELLO", required = true)
    private String word;
    
    @Schema(description = "List of positions where each letter is placed (0-based index)", example = "[0, 1, 2, 3, 4]")
    private List<Integer> positions;
    
    @Schema(description = "List of special tile types for each position", 
            example = "[\"normal\", \"dl\", \"normal\", \"tw\", \"normal\"]",
            allowableValues = {"normal", "dl", "tl", "dw", "tw"})
    private List<String> specialTiles;
    
    public CalculateScoreRequest() {}
    
    public CalculateScoreRequest(String word) {
        this.word = word;
    }
    
    public CalculateScoreRequest(String word, List<Integer> positions, List<String> specialTiles) {
        this.word = word;
        this.positions = positions;
        this.specialTiles = specialTiles;
    }
    
    public String getWord() {
        return word;
    }
    
    public void setWord(String word) {
        this.word = word;
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