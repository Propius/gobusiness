package com.govtech.scrabble.dto;

import com.govtech.scrabble.entity.Score;

import java.time.LocalDateTime;

public class ScoreResponse {
    
    private Long id;
    private String word;
    private Integer points;
    private LocalDateTime createdAt;
    
    public ScoreResponse() {}
    
    public ScoreResponse(Score score) {
        this.id = score.getId();
        this.word = score.getWord();
        this.points = score.getPoints();
        this.createdAt = score.getCreatedAt();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getWord() {
        return word;
    }
    
    public void setWord(String word) {
        this.word = word;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}