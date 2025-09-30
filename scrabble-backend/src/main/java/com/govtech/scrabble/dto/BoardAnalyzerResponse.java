package com.govtech.scrabble.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response for board analyzer feature containing top scoring word combinations")
public class BoardAnalyzerResponse {
    
    @Schema(description = "Top 10 highest scoring word combinations")
    private List<WordCombination> topCombinations;
    
    @Schema(description = "Total number of valid combinations found")
    private int totalCombinationsCount;
    
    @Schema(description = "Message describing the analysis result")
    private String message;
    
    public BoardAnalyzerResponse() {}
    
    public BoardAnalyzerResponse(List<WordCombination> topCombinations, int totalCombinationsCount, String message) {
        this.topCombinations = topCombinations;
        this.totalCombinationsCount = totalCombinationsCount;
        this.message = message;
    }
    
    public List<WordCombination> getTopCombinations() {
        return topCombinations;
    }
    
    public void setTopCombinations(List<WordCombination> topCombinations) {
        this.topCombinations = topCombinations;
    }
    
    public int getTotalCombinationsCount() {
        return totalCombinationsCount;
    }
    
    public void setTotalCombinationsCount(int totalCombinationsCount) {
        this.totalCombinationsCount = totalCombinationsCount;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Schema(description = "A word combination that can be played on the board")
    public static class WordCombination {
        
        @Schema(description = "The word to be played")
        private String word;
        
        @Schema(description = "Total score for this word including bonuses")
        private int totalScore;
        
        @Schema(description = "Starting row position (0-based)")
        private int startRow;
        
        @Schema(description = "Starting column position (0-based)")
        private int startCol;
        
        @Schema(description = "Direction: HORIZONTAL or VERTICAL")
        private String direction;
        
        @Schema(description = "Hand tiles used for this word")
        private List<String> usedHandTiles;
        
        @Schema(description = "Board positions used (row,col pairs)")
        private List<BoardPosition> boardPositions;
        
        @Schema(description = "Special tile bonuses applied")
        private List<String> bonusesApplied;
        
        public WordCombination() {}
        
        public WordCombination(String word, int totalScore, int startRow, int startCol, 
                             String direction, List<String> usedHandTiles, 
                             List<BoardPosition> boardPositions, List<String> bonusesApplied) {
            this.word = word;
            this.totalScore = totalScore;
            this.startRow = startRow;
            this.startCol = startCol;
            this.direction = direction;
            this.usedHandTiles = usedHandTiles;
            this.boardPositions = boardPositions;
            this.bonusesApplied = bonusesApplied;
        }
        
        public String getWord() { return word; }
        public void setWord(String word) { this.word = word; }
        
        public int getTotalScore() { return totalScore; }
        public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
        
        public int getStartRow() { return startRow; }
        public void setStartRow(int startRow) { this.startRow = startRow; }
        
        public int getStartCol() { return startCol; }
        public void setStartCol(int startCol) { this.startCol = startCol; }
        
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }
        
        public List<String> getUsedHandTiles() { return usedHandTiles; }
        public void setUsedHandTiles(List<String> usedHandTiles) { this.usedHandTiles = usedHandTiles; }
        
        public List<BoardPosition> getBoardPositions() { return boardPositions; }
        public void setBoardPositions(List<BoardPosition> boardPositions) { this.boardPositions = boardPositions; }
        
        public List<String> getBonusesApplied() { return bonusesApplied; }
        public void setBonusesApplied(List<String> bonusesApplied) { this.bonusesApplied = bonusesApplied; }
    }
    
    @Schema(description = "A position on the board")
    public static class BoardPosition {
        
        @Schema(description = "Row position (0-based)")
        private int row;
        
        @Schema(description = "Column position (0-based)")
        private int col;
        
        @Schema(description = "Letter at this position")
        private String letter;
        
        @Schema(description = "Whether this position uses a hand tile")
        private boolean usesHandTile;
        
        public BoardPosition() {}
        
        public BoardPosition(int row, int col, String letter, boolean usesHandTile) {
            this.row = row;
            this.col = col;
            this.letter = letter;
            this.usesHandTile = usesHandTile;
        }
        
        public int getRow() { return row; }
        public void setRow(int row) { this.row = row; }
        
        public int getCol() { return col; }
        public void setCol(int col) { this.col = col; }
        
        public String getLetter() { return letter; }
        public void setLetter(String letter) { this.letter = letter; }
        
        public boolean isUsesHandTile() { return usesHandTile; }
        public void setUsesHandTile(boolean usesHandTile) { this.usesHandTile = usesHandTile; }
    }
}