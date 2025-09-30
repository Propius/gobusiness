package com.govtech.scrabble.service;

import com.govtech.scrabble.dto.ScrambleCheckResponse;
import com.govtech.scrabble.dto.ScrambleResponse;

/**
 * Interface for word scramble game operations
 * Provides functionality for generating, reshuffling, and checking scrambled words
 */
public interface WordScrambleService {
    
    /**
     * Check if scramble feature is enabled
     * @return true if scramble is enabled
     */
    boolean isScrambleEnabled();
    
    /**
     * Generate a new word scramble
     * @return ScrambleResponse with scrambled word and game data
     */
    ScrambleResponse generateScramble();
    
    /**
     * Generate a new word scramble with specific difficulty
     * @param difficulty Difficulty level (easy, medium, hard)
     * @return ScrambleResponse with scrambled word and game data
     */
    ScrambleResponse generateScramble(String difficulty);
    
    /**
     * Reshuffle the letters of an active scramble session
     * @param sessionId The scramble session identifier
     * @return ScrambleResponse with reshuffled letters
     * @throws IllegalArgumentException if session is invalid or expired
     */
    ScrambleResponse reshuffleScramble(String sessionId);
    
    /**
     * Check if the user's answer is correct
     * @param sessionId The scramble session identifier
     * @param userAnswer The user's proposed answer
     * @return ScrambleCheckResponse with result and score
     */
    ScrambleCheckResponse checkAnswer(String sessionId, String userAnswer);
    
    /**
     * Clear an active scramble session
     * @param sessionId The session to clear
     */
    void clearSession(String sessionId);
    
    /**
     * Get the count of active scramble sessions
     * @return Number of active sessions
     */
    int getActiveSessionsCount();
}