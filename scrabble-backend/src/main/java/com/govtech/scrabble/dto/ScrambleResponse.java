package com.govtech.scrabble.dto;

import java.util.List;

public class ScrambleResponse {
    
    private String scrambledLetters;
    private String originalWord;
    private List<Character> availableLetters;
    private Integer wordLength;
    private String difficulty;
    private String hint;
    
    public ScrambleResponse() {}
    
    public ScrambleResponse(String scrambledLetters, String originalWord, List<Character> availableLetters, 
                           Integer wordLength, String difficulty, String hint) {
        this.scrambledLetters = scrambledLetters;
        this.originalWord = originalWord;
        this.availableLetters = availableLetters;
        this.wordLength = wordLength;
        this.difficulty = difficulty;
        this.hint = hint;
    }
    
    public String getScrambledLetters() {
        return scrambledLetters;
    }
    
    public void setScrambledLetters(String scrambledLetters) {
        this.scrambledLetters = scrambledLetters;
    }
    
    public String getOriginalWord() {
        return originalWord;
    }
    
    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }
    
    public List<Character> getAvailableLetters() {
        return availableLetters;
    }
    
    public void setAvailableLetters(List<Character> availableLetters) {
        this.availableLetters = availableLetters;
    }
    
    public Integer getWordLength() {
        return wordLength;
    }
    
    public void setWordLength(Integer wordLength) {
        this.wordLength = wordLength;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getHint() {
        return hint;
    }
    
    public void setHint(String hint) {
        this.hint = hint;
    }
}