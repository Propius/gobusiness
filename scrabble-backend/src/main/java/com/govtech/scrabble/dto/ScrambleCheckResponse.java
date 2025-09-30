package com.govtech.scrabble.dto;

public class ScrambleCheckResponse {
    
    private Boolean isCorrect;
    private String userAnswer;
    private String correctAnswer;
    private Integer score;
    private String message;
    
    public ScrambleCheckResponse() {}
    
    public ScrambleCheckResponse(Boolean isCorrect, String userAnswer, String correctAnswer, Integer score, String message) {
        this.isCorrect = isCorrect;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.score = score;
        this.message = message;
    }
    
    public Boolean getIsCorrect() {
        return isCorrect;
    }
    
    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    
    public String getUserAnswer() {
        return userAnswer;
    }
    
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}