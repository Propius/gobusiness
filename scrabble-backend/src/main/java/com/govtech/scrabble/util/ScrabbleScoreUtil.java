package com.govtech.scrabble.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for Scrabble scoring calculations
 * Provides centralized letter scoring and word score calculation functionality
 */
public final class ScrabbleScoreUtil {
    
    private static final Map<Character, Integer> LETTER_SCORES = new HashMap<>();
    
    static {
        // 1 point letters
        "AEIOULNSTR".chars().forEach(c -> LETTER_SCORES.put((char) c, 1));
        
        // 2 point letters
        "DG".chars().forEach(c -> LETTER_SCORES.put((char) c, 2));
        
        // 3 point letters
        "BCMP".chars().forEach(c -> LETTER_SCORES.put((char) c, 3));
        
        // 4 point letters
        "FHVWY".chars().forEach(c -> LETTER_SCORES.put((char) c, 4));
        
        // 5 point letters
        LETTER_SCORES.put('K', 5);
        
        // 8 point letters
        "JX".chars().forEach(c -> LETTER_SCORES.put((char) c, 8));
        
        // 10 point letters
        "QZ".chars().forEach(c -> LETTER_SCORES.put((char) c, 10));
    }
    
    private ScrabbleScoreUtil() {
        // Utility class should not be instantiated
    }
    
    /**
     * Get the Scrabble point value for a single letter
     * @param letter The letter to score (case-insensitive)
     * @return The point value of the letter, or 0 if not found
     */
    public static int getLetterScore(char letter) {
        return LETTER_SCORES.getOrDefault(Character.toUpperCase(letter), 0);
    }
    
    /**
     * Calculate the total Scrabble score for a word (without special tiles)
     * @param word The word to score (case-insensitive)
     * @return Total score for all letters in the word
     */
    public static int calculateWordScore(String word) {
        if (word == null || word.trim().isEmpty()) {
            return 0;
        }
        
        return word.toUpperCase().chars()
                .mapToObj(c -> (char) c)
                .filter(Character::isLetter)
                .mapToInt(ScrabbleScoreUtil::getLetterScore)
                .sum();
    }
    
    /**
     * Calculate Scrabble score for a word with special tiles
     * @param word The word to score (case-insensitive)
     * @param positions List of positions where each letter is placed
     * @param specialTiles List of special tile types for each position ("normal", "dl", "tl", "dw", "tw")
     * @return Total score including special tile bonuses
     */
    public static int calculateWordScoreWithSpecialTiles(String word, List<Integer> positions, List<String> specialTiles) {
        if (word == null || word.trim().isEmpty() || positions == null || specialTiles == null) {
            return calculateWordScore(word);
        }

        String normalizedWord = word.toUpperCase().trim();
        int baseScore = 0;
        SpecialTileMultiplier multiplierTracker = new SpecialTileMultiplier();

        // Calculate score for each letter position
        for (int i = 0; i < normalizedWord.length(); i++) {
            char letter = normalizedWord.charAt(i);
            if (!Character.isLetter(letter)) {
                continue;
            }

            int letterScore = getLetterScore(letter);

            // Apply special tile bonuses if position is within bounds
            if (i < positions.size()) {
                int position = positions.get(i);
                if (position >= 0 && position < specialTiles.size()) {
                    String specialTileType = specialTiles.get(position);
                    letterScore = applySpecialTileMultiplier(letterScore, specialTileType, multiplierTracker);
                }
            }

            baseScore += letterScore;
        }

        return baseScore * multiplierTracker.getWordMultiplier();
    }
    
    /**
     * Calculate Scrabble score for a word placed on specific board positions
     * @param word The word to score
     * @param boardPositions List of board positions (row, col pairs)
     * @param specialTilesBoard 2D array of special tile types
     * @param usesHandTileAtPosition List indicating which positions use hand tiles (only these get special tile bonuses)
     * @return Total score including special tile bonuses
     */
    public static int calculateBoardWordScore(String word, List<int[]> boardPositions,
                                            String[][] specialTilesBoard, List<Boolean> usesHandTileAtPosition) {
        if (word == null || word.trim().isEmpty() || boardPositions == null || specialTilesBoard == null) {
            return calculateWordScore(word);
        }

        String normalizedWord = word.toUpperCase().trim();
        int baseScore = 0;
        SpecialTileMultiplier multiplierTracker = new SpecialTileMultiplier();

        // Calculate score for each letter position
        for (int i = 0; i < normalizedWord.length(); i++) {
            char letter = normalizedWord.charAt(i);
            if (!Character.isLetter(letter)) {
                continue;
            }

            int letterScore = getLetterScore(letter);

            // Apply special tile bonuses only if using a hand tile
            if (i < boardPositions.size() && i < usesHandTileAtPosition.size() && usesHandTileAtPosition.get(i)) {
                int[] position = boardPositions.get(i);
                if (position.length >= 2) {
                    int row = position[0];
                    int col = position[1];

                    if (row >= 0 && row < specialTilesBoard.length &&
                        col >= 0 && col < specialTilesBoard[row].length) {
                        String specialTileType = specialTilesBoard[row][col];
                        letterScore = applySpecialTileMultiplier(letterScore, specialTileType, multiplierTracker);
                    }
                }
            }

            baseScore += letterScore;
        }

        return baseScore * multiplierTracker.getWordMultiplier();
    }
    
    /**
     * Get a copy of all letter scores
     * @return Map of letter to score values
     */
    public static Map<Character, Integer> getAllLetterScores() {
        return new HashMap<>(LETTER_SCORES);
    }

    /**
     * Helper class to track and apply word multipliers during special tile calculation
     * This enables centralized special tile logic without duplicating switch-case statements
     */
    public static class SpecialTileMultiplier {
        private int wordMultiplier = 1;

        public void applyWordMultiplier(int multiplier) {
            this.wordMultiplier *= multiplier;
        }

        public int getWordMultiplier() {
            return wordMultiplier;
        }
    }

    /**
     * Apply special tile multiplier to letter score and track word multipliers
     * This centralizes the special tile logic to avoid code duplication
     *
     * @param letterScore The base letter score
     * @param specialTileType The special tile type ("dl", "tl", "dw", "tw", "normal", or full names)
     * @param multiplierTracker Tracker to accumulate word multipliers
     * @return The letter score after applying letter multipliers
     */
    public static int applySpecialTileMultiplier(int letterScore, String specialTileType,
                                                SpecialTileMultiplier multiplierTracker) {
        if (specialTileType == null || multiplierTracker == null) {
            return letterScore;
        }

        switch (specialTileType) {
            case "double_letter", "dl" -> {
                return letterScore * 2; // Double letter score
            }
            case "triple_letter", "tl" -> {
                return letterScore * 3; // Triple letter score
            }
            case "double_word", "dw" -> {
                multiplierTracker.applyWordMultiplier(2); // Double word score
                return letterScore; // Word multiplier applied at end
            }
            case "triple_word", "tw" -> {
                multiplierTracker.applyWordMultiplier(3); // Triple word score
                return letterScore; // Word multiplier applied at end
            }
            default -> {
                return letterScore; // "normal" or unrecognized - no multiplier
            }
        }
    }
}