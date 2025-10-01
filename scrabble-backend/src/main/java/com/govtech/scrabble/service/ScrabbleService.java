package com.govtech.scrabble.service;

import com.govtech.scrabble.dto.CalculateScoreRequest;
import com.govtech.scrabble.dto.CalculateScoreResponse;
import com.govtech.scrabble.dto.ScoreRequest;
import com.govtech.scrabble.dto.ScoreResponse;

import java.util.List;

/**
 * Interface for Scrabble game operations
 * Provides word scoring, validation, and score persistence functionality
 */
public interface ScrabbleService {

    /**
     * Calculate the Scrabble score for a given word (basic calculation)
     * @param word The word to score
     * @return CalculateScoreResponse containing the score and validation results
     */
    CalculateScoreResponse calculateScore(String word);

    /**
     * Calculate the Scrabble score with special tiles
     * @param request The request containing word, positions, and special tile information
     * @return CalculateScoreResponse containing the enhanced score and validation results
     */
    CalculateScoreResponse calculateScoreWithSpecialTiles(CalculateScoreRequest request);

    /**
     * Save a word score to the database
     * @param word The word to save
     * @return ScoreResponse containing the saved score
     * @throws IllegalArgumentException if the word is invalid
     */
    ScoreResponse saveScore(String word);

    /**
     * Save a word score with enhanced score and special tile data to the database
     * @param request The request containing word, enhanced score, positions, and special tiles
     * @return ScoreResponse containing the saved score
     * @throws IllegalArgumentException if the word or score is invalid
     */
    ScoreResponse saveScoreWithEnhancement(ScoreRequest request);

    /**
     * Get the top 10 highest scores from the database
     * @return List of top scores
     */
    List<ScoreResponse> getTopScores();

    /**
     * Validate a word without calculating score
     * @param word The word to validate
     * @return CalculateScoreResponse containing only validation results
     */
    CalculateScoreResponse validateWordOnly(String word);
}