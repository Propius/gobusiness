package com.govtech.scrabble.dto;

import java.util.List;

public class WordFinderResponse {
    private List<PossibleWord> possibleWords;
    private int totalFound;
    private String message;
    
    public WordFinderResponse() {}
    
    public WordFinderResponse(List<PossibleWord> possibleWords, int totalFound, String message) {
        this.possibleWords = possibleWords;
        this.totalFound = totalFound;
        this.message = message;
    }
    
    public List<PossibleWord> getPossibleWords() {
        return possibleWords;
    }
    
    public void setPossibleWords(List<PossibleWord> possibleWords) {
        this.possibleWords = possibleWords;
    }
    
    public int getTotalFound() {
        return totalFound;
    }
    
    public void setTotalFound(int totalFound) {
        this.totalFound = totalFound;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public static class PossibleWord {
        private String word;
        private int score;
        private List<Integer> positions; // Positions where letters are placed (0-based)
        private List<String> usedHandTiles; // Which hand tiles were used
        private List<String> usedBoardTiles; // Which board tiles were used
        
        public PossibleWord() {}
        
        public PossibleWord(String word, int score, List<Integer> positions, List<String> usedHandTiles, List<String> usedBoardTiles) {
            this.word = word;
            this.score = score;
            this.positions = positions;
            this.usedHandTiles = usedHandTiles;
            this.usedBoardTiles = usedBoardTiles;
        }
        
        public String getWord() {
            return word;
        }
        
        public void setWord(String word) {
            this.word = word;
        }
        
        public int getScore() {
            return score;
        }
        
        public void setScore(int score) {
            this.score = score;
        }
        
        public List<Integer> getPositions() {
            return positions;
        }
        
        public void setPositions(List<Integer> positions) {
            this.positions = positions;
        }
        
        public List<String> getUsedHandTiles() {
            return usedHandTiles;
        }
        
        public void setUsedHandTiles(List<String> usedHandTiles) {
            this.usedHandTiles = usedHandTiles;
        }
        
        public List<String> getUsedBoardTiles() {
            return usedBoardTiles;
        }
        
        public void setUsedBoardTiles(List<String> usedBoardTiles) {
            this.usedBoardTiles = usedBoardTiles;
        }
    }
}