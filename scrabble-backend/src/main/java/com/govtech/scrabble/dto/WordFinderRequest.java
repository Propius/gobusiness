package com.govtech.scrabble.dto;

import com.govtech.scrabble.validation.ValidWord;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request for finding possible words on a Scrabble board")
public class WordFinderRequest {
    
    @NotNull(message = "Board tiles cannot be null")
    @Size(min = 1, max = 25, message = "Board tiles must have between 1 and 25 positions")
    @Schema(description = "Fixed letters on board (empty string for empty positions)", 
            example = "[\"\", \"\", \"P\", \"H\", \"O\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\"]")
    private List<String> boardTiles;
    
    @NotNull(message = "Hand tiles cannot be null")
    @Size(min = 1, max = 7, message = "Hand tiles must have between 1 and 7 letters")
    @Schema(description = "Letters player is holding", 
            example = "[\"L\", \"D\", \"E\", \"R\", \"S\"]")
    private List<String> handTiles;
    
    public WordFinderRequest() {}
    
    public WordFinderRequest(List<String> boardTiles, List<String> handTiles) {
        this.boardTiles = boardTiles;
        this.handTiles = handTiles;
    }
    
    public List<String> getBoardTiles() {
        return boardTiles;
    }
    
    public void setBoardTiles(List<String> boardTiles) {
        this.boardTiles = boardTiles;
    }
    
    public List<String> getHandTiles() {
        return handTiles;
    }
    
    public void setHandTiles(List<String> handTiles) {
        this.handTiles = handTiles;
    }
}