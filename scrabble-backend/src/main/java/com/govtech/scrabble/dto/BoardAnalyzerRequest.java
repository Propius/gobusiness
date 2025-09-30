package com.govtech.scrabble.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Request for board analyzer feature")
public class BoardAnalyzerRequest {
    
    @Schema(description = "Letters on the board (15x15 grid, null or empty string for empty positions)", example = "[\"T\", \"\", \"A\", ...]")
    private List<String> boardLetters;
    
    @Schema(description = "Letters in hand", example = "[\"R\", \"A\", \"C\", \"E\", \"S\", \"T\", \"O\"]")
    private List<String> handLetters;
    
    @Schema(description = "Special tile types for board positions (15x15 grid)", example = "[\"normal\", \"dl\", \"tw\", ...]")
    private List<String> specialTiles;
    
    public BoardAnalyzerRequest() {}
    
    public BoardAnalyzerRequest(List<String> boardLetters, List<String> handLetters) {
        this.boardLetters = boardLetters;
        this.handLetters = handLetters;
    }
    
    public BoardAnalyzerRequest(List<String> boardLetters, List<String> handLetters, List<String> specialTiles) {
        this.boardLetters = boardLetters;
        this.handLetters = handLetters;
        this.specialTiles = specialTiles;
    }
    
    public List<String> getBoardLetters() {
        return boardLetters;
    }
    
    public void setBoardLetters(List<String> boardLetters) {
        this.boardLetters = boardLetters;
    }
    
    public List<String> getHandLetters() {
        return handLetters;
    }
    
    public void setHandLetters(List<String> handLetters) {
        this.handLetters = handLetters;
    }
    
    public List<String> getSpecialTiles() {
        return specialTiles;
    }
    
    public void setSpecialTiles(List<String> specialTiles) {
        this.specialTiles = specialTiles;
    }
}