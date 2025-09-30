package com.govtech.scrabble.dto;

import java.util.Map;

public class LetterScoringResponse {
    private boolean enabled = true;
    private Map<String, Integer> letterScores;
    private String message;

    public LetterScoringResponse() {}

    public LetterScoringResponse(Map<String, Integer> letterScores, String message) {
        this.letterScores = letterScores;
        this.message = message;
        this.enabled = true;
    }

    public LetterScoringResponse(boolean enabled, Map<String, Integer> letterScores, String message) {
        this.enabled = enabled;
        this.letterScores = letterScores;
        this.message = message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, Integer> getLetterScores() {
        return letterScores;
    }

    public void setLetterScores(Map<String, Integer> letterScores) {
        this.letterScores = letterScores;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}