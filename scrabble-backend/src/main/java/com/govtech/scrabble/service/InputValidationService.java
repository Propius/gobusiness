package com.govtech.scrabble.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for validating input parameters that cannot use Bean Validation annotations.
 */
@Service
public class InputValidationService {

    private static final Pattern VALID_WORD_PATTERN = Pattern.compile("^[A-Za-z]+$");
    private static final Pattern VALID_SPECIAL_TILE_PATTERN = Pattern.compile("^(normal|dl|tl|dw|tw)$");
    // Maximum word length matches database schema constraint (Score entity: varchar(10))
    private static final int MAX_WORD_LENGTH = 10;
    // Maximum positions for Scrabble board (15x15 = 225 squares, but realistically max word length is 15)
    private static final int MAX_POSITIONS = 25;

    /**
     * Validates a word parameter
     */
    public void validateWord(String word) {
        if (!StringUtils.hasText(word)) {
            throw new IllegalArgumentException("Word cannot be empty");
        }

        String trimmed = word.trim();
        
        if (trimmed.length() > MAX_WORD_LENGTH) {
            throw new IllegalArgumentException("Word cannot exceed " + MAX_WORD_LENGTH + " characters");
        }

        if (!VALID_WORD_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Word must contain only alphabetic characters (A-Z)");
        }
    }

    /**
     * Validates positions parameter
     */
    public void validatePositions(List<Integer> positions) {
        if (positions == null) {
            return; // Optional parameter
        }

        if (positions.size() > MAX_POSITIONS) {
            throw new IllegalArgumentException("Too many positions specified");
        }

        for (int i = 0; i < positions.size(); i++) {
            Integer pos = positions.get(i);
            if (pos == null || pos < 0 || pos >= MAX_POSITIONS) {
                throw new IllegalArgumentException("Position " + i + " is invalid. Must be between 0 and " + (MAX_POSITIONS - 1));
            }
        }
    }

    /**
     * Validates special tiles parameter
     */
    public void validateSpecialTiles(List<String> specialTiles) {
        if (specialTiles == null) {
            return; // Optional parameter
        }

        if (specialTiles.size() > MAX_POSITIONS) {
            throw new IllegalArgumentException("Too many special tiles specified");
        }

        for (int i = 0; i < specialTiles.size(); i++) {
            String tile = specialTiles.get(i);
            if (tile != null && !VALID_SPECIAL_TILE_PATTERN.matcher(tile).matches()) {
                throw new IllegalArgumentException("Special tile at position " + i + " is invalid. Must be one of: normal, dl, tl, dw, tw");
            }
        }
    }

    /**
     * Validates that positions and special tiles lists have matching lengths
     */
    public void validatePositionsAndTilesMatch(List<Integer> positions, List<String> specialTiles) {
        if (positions != null && specialTiles != null) {
            if (positions.size() != specialTiles.size()) {
                throw new IllegalArgumentException("Positions and special tiles lists must have the same length");
            }
        }
    }

    /**
     * Validates difficulty parameter for scramble game
     */
    public void validateDifficulty(String difficulty) {
        if (difficulty != null) {
            String normalizedDifficulty = difficulty.toLowerCase().trim();
            if (!normalizedDifficulty.matches("^(easy|medium|hard)$")) {
                throw new IllegalArgumentException("Difficulty must be one of: easy, medium, hard");
            }
        }
    }
}