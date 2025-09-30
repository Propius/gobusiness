package com.govtech.scrabble.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Entity representing a saved board state for the board analyzer feature.
 * Stores the current state of a 15x15 Scrabble board along with player's hand tiles.
 */
@Entity
@Table(name = "board_states", indexes = {
    @Index(name = "idx_user_session", columnList = "user_session"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class BoardState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_session", length = 255)
    private String userSession;

    @NotBlank(message = "Board data cannot be blank")
    @Column(name = "board_data", columnDefinition = "TEXT", nullable = false)
    private String boardData; // JSON representation of 15x15 board

    @Size(max = 10, message = "Hand tiles cannot exceed 10 characters")
    @Column(name = "hand_tiles", length = 10)
    private String handTiles;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @Column(name = "analysis_score")
    private Integer analysisScore;

    /**
     * Default constructor initializing timestamps.
     */
    public BoardState() {
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.analysisScore = 0;
    }

    /**
     * Constructor with required fields.
     *
     * @param userSession  The user session identifier
     * @param boardData    JSON representation of the board state
     * @param handTiles    The tiles in the player's hand
     */
    public BoardState(String userSession, String boardData, String handTiles) {
        this();
        this.userSession = userSession;
        this.boardData = boardData;
        this.handTiles = handTiles;
    }

    /**
     * Updates the last modified timestamp.
     * Should be called whenever the board state is modified.
     */
    @PreUpdate
    protected void onUpdate() {
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Updates board data and sets last modified timestamp.
     *
     * @param boardData  New board data to set
     */
    public void updateBoardData(String boardData) {
        this.boardData = boardData;
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Updates hand tiles and sets last modified timestamp.
     *
     * @param handTiles  New hand tiles to set
     */
    public void updateHandTiles(String handTiles) {
        this.handTiles = handTiles;
        this.lastModified = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }

    public String getBoardData() {
        return boardData;
    }

    public void setBoardData(String boardData) {
        this.boardData = boardData;
    }

    public String getHandTiles() {
        return handTiles;
    }

    public void setHandTiles(String handTiles) {
        this.handTiles = handTiles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getAnalysisScore() {
        return analysisScore;
    }

    public void setAnalysisScore(Integer analysisScore) {
        this.analysisScore = analysisScore;
    }

    @Override
    public String toString() {
        return "BoardState{" +
                "id=" + id +
                ", userSession='" + userSession + '\'' +
                ", boardData='" + (boardData != null ? boardData.substring(0, Math.min(50, boardData.length())) + "..." : "null") + '\'' +
                ", handTiles='" + handTiles + '\'' +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                ", analysisScore=" + analysisScore +
                '}';
    }
}