package com.govtech.scrabble.service.impl;

import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.dto.BoardAnalyzerRequest;
import com.govtech.scrabble.dto.BoardAnalyzerResponse;
import com.govtech.scrabble.service.BoardAnalyzerService;
import com.govtech.scrabble.service.EnglishDictionaryService;
import com.govtech.scrabble.util.ScrabbleScoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardAnalyzerServiceImpl implements BoardAnalyzerService {

    private static final Logger logger = LoggerFactory.getLogger(BoardAnalyzerServiceImpl.class);

    private final ScrabbleProperties scrabbleProperties;
    private final EnglishDictionaryService englishDictionaryService;

    public BoardAnalyzerServiceImpl(ScrabbleProperties scrabbleProperties,
                              EnglishDictionaryService englishDictionaryService) {
        this.scrabbleProperties = scrabbleProperties;
        this.englishDictionaryService = englishDictionaryService;
    }
    
    public boolean isBoardAnalyzerEnabled() {
        return scrabbleProperties.getBoardAnalyzer().isEnabled();
    }
    
    public BoardAnalyzerResponse analyzeBoardForTopCombinations(BoardAnalyzerRequest request) {
        if (!isBoardAnalyzerEnabled()) {
            throw new IllegalStateException("Board analyzer feature is disabled");
        }
        
        logger.info("Analyzing board for top scoring combinations");
        
        List<String> boardLetters = request.getBoardLetters() != null ? request.getBoardLetters() : new ArrayList<>();
        List<String> handLetters = request.getHandLetters() != null ? request.getHandLetters() : new ArrayList<>();
        List<String> specialTiles = request.getSpecialTiles() != null ? request.getSpecialTiles() : new ArrayList<>();
        
        logger.info("Request received - Board letters: {}, Hand letters: {}, Special tiles enabled: {}", 
                   boardLetters.size(), handLetters.size(), scrabbleProperties.getSpecialTiles().getBoardAnalyzer().isEnabled());
        logger.debug("Special tiles data: {}", specialTiles.size() > 0 ? specialTiles.subList(0, Math.min(10, specialTiles.size())) : "empty");
        
        // Normalize inputs
        handLetters = handLetters.stream()
                .filter(Objects::nonNull)
                .map(s -> s.toUpperCase().trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        if (handLetters.isEmpty()) {
            return new BoardAnalyzerResponse(new ArrayList<>(), 0, "No hand tiles provided");
        }
        
        int boardSize = scrabbleProperties.getTiles().getBoardAnalyzer().getBoardSize();
        
        // Ensure board has correct size
        while (boardLetters.size() < boardSize * boardSize) {
            boardLetters.add("");
        }
        
        // Normalize board letters
        boardLetters = boardLetters.stream()
                .limit(boardSize * boardSize)
                .map(s -> s != null ? s.toUpperCase().trim() : "")
                .collect(Collectors.toList());
        
        // Convert board to 2D array for easier processing
        String[][] board = new String[boardSize][boardSize];
        for (int i = 0; i < boardSize * boardSize; i++) {
            int row = i / boardSize;
            int col = i % boardSize;
            board[row][col] = boardLetters.get(i);
        }
        
        // Initialize special tiles board from request
        String[][] specialTilesBoard = new String[boardSize][boardSize];
        
        // Ensure special tiles has correct size
        while (specialTiles.size() < boardSize * boardSize) {
            specialTiles.add("normal");
        }
        
        // Convert special tiles to 2D array
        for (int i = 0; i < boardSize * boardSize && i < specialTiles.size(); i++) {
            int row = i / boardSize;
            int col = i % boardSize;
            specialTilesBoard[row][col] = specialTiles.get(i) != null ? specialTiles.get(i) : "normal";
        }
        logger.debug("Using special tiles from request");
        
        // Find all possible word combinations
        List<BoardAnalyzerResponse.WordCombination> allCombinations = findAllWordCombinations(board, handLetters, specialTilesBoard);
        
        // Sort by score (descending)
        allCombinations.sort((c1, c2) -> Integer.compare(c2.getTotalScore(), c1.getTotalScore()));
        
        // Get top 10
        List<BoardAnalyzerResponse.WordCombination> topCombinations = allCombinations.stream()
                .limit(10)
                .collect(Collectors.toList());
        
        String message = allCombinations.isEmpty() ? 
                "No valid word combinations found" :
                String.format("Found %d valid combinations (showing top %d)", 
                             allCombinations.size(), topCombinations.size());
        
        logger.info("Board analysis completed: {} total combinations, top score: {}", 
                   allCombinations.size(), 
                   topCombinations.isEmpty() ? 0 : topCombinations.get(0).getTotalScore());
        
        return new BoardAnalyzerResponse(topCombinations, allCombinations.size(), message);
    }
    
    private List<BoardAnalyzerResponse.WordCombination> findAllWordCombinations(String[][] board, List<String> handLetters, String[][] specialTiles) {
        List<BoardAnalyzerResponse.WordCombination> combinations = new ArrayList<>();
        int boardSize = board.length;
        
        // Combine hand letters with board letters for dictionary search
        List<String> allAvailableLetters = new ArrayList<>(handLetters);
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (!board[row][col].isEmpty()) {
                    allAvailableLetters.add(board[row][col]);
                }
            }
        }
        
        if (allAvailableLetters.isEmpty()) {
            return combinations;
        }
        
        // Get possible words from dictionary
        int minLength = 2;
        int maxLength = Math.min(boardSize, allAvailableLetters.size());

        List<String> possibleWords = englishDictionaryService.findPossibleWords(
            allAvailableLetters, minLength, maxLength);
        
        // For each word, try to place it on the board
        for (String word : possibleWords) {
            // Try horizontal placements
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col <= boardSize - word.length(); col++) {
                    if (wordConnectsToExistingTiles(board, word, row, col, "HORIZONTAL", handLetters)) {
                        BoardAnalyzerResponse.WordCombination combination = 
                            tryPlaceWordHorizontally(board, word, row, col, handLetters, specialTiles);
                        if (combination != null) {
                            combinations.add(combination);
                        }
                    }
                }
            }
            
            // Try vertical placements
            for (int row = 0; row <= boardSize - word.length(); row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (wordConnectsToExistingTiles(board, word, row, col, "VERTICAL", handLetters)) {
                        BoardAnalyzerResponse.WordCombination combination = 
                            tryPlaceWordVertically(board, word, row, col, handLetters, specialTiles);
                        if (combination != null) {
                            combinations.add(combination);
                        }
                    }
                }
            }
        }
        
        return combinations;
    }
    
    private BoardAnalyzerResponse.WordCombination tryPlaceWordHorizontally(String[][] board, String word,
                                                                          int startRow, int startCol,
                                                                          List<String> handLetters, String[][] specialTiles) {
        List<String> availableHandTiles = new ArrayList<>(handLetters);
        List<String> usedHandTiles = new ArrayList<>();
        List<BoardAnalyzerResponse.BoardPosition> positions = new ArrayList<>();
        boolean usedAtLeastOneHandTile = false;
        boolean connectsToExistingTile = false;

        // Check if word can be placed
        for (int i = 0; i < word.length(); i++) {
            char wordChar = word.charAt(i);
            int col = startCol + i;
            String boardTile = board[startRow][col];

            if (!boardTile.isEmpty()) {
                // Position has existing tile
                if (boardTile.equals(String.valueOf(wordChar))) {
                    // Use board tile
                    positions.add(new BoardAnalyzerResponse.BoardPosition(startRow, col, boardTile, false));
                    connectsToExistingTile = true;
                } else {
                    // Board tile doesn't match
                    return null;
                }
            } else {
                // Need hand tile
                String neededTile = String.valueOf(wordChar);
                if (availableHandTiles.contains(neededTile)) {
                    availableHandTiles.remove(neededTile);
                    usedHandTiles.add(neededTile);
                    positions.add(new BoardAnalyzerResponse.BoardPosition(startRow, col, neededTile, true));
                    usedAtLeastOneHandTile = true;
                } else {
                    return null;
                }
            }
        }

        // Must use at least one hand tile to be a valid play
        if (!usedAtLeastOneHandTile) {
            return null;
        }

        // CRITICAL: Validate the complete word formed (including tiles before and after)
        // This prevents invalid words like "ATLOPHO" when placing "LOP" between "AT" and "PHO"
        String completeWord = extractCompleteWordFormedHorizontally(word, startRow, startCol, board);
        if (!completeWord.equals(word)) {
            // Word extends existing tiles - validate complete word is also valid
            if (!englishDictionaryService.isValidWord(completeWord)) {
                logger.debug("Rejecting '{}' at position ({},{}) - forms invalid complete word '{}'",
                    word, startRow, startCol, completeWord);
                return null;
            }
            logger.debug("Validated complete word '{}' formed by placing '{}' at position ({},{})",
                completeWord, word, startRow, startCol);
        }

        // Calculate score
        int score = calculateWordScore(word, positions, specialTiles);

        List<String> bonusesApplied = new ArrayList<>();
        if (scrabbleProperties.getSpecialTiles().getBoardAnalyzer().isEnabled()) {
            bonusesApplied.add("Special tiles considered");
        }

        return new BoardAnalyzerResponse.WordCombination(
            word, score, startRow, startCol, "HORIZONTAL",
            usedHandTiles, positions, bonusesApplied);
    }
    
    private BoardAnalyzerResponse.WordCombination tryPlaceWordVertically(String[][] board, String word,
                                                                        int startRow, int startCol,
                                                                        List<String> handLetters, String[][] specialTiles) {
        List<String> availableHandTiles = new ArrayList<>(handLetters);
        List<String> usedHandTiles = new ArrayList<>();
        List<BoardAnalyzerResponse.BoardPosition> positions = new ArrayList<>();
        boolean usedAtLeastOneHandTile = false;
        boolean connectsToExistingTile = false;

        // Check if word can be placed
        for (int i = 0; i < word.length(); i++) {
            char wordChar = word.charAt(i);
            int row = startRow + i;
            String boardTile = board[row][startCol];

            if (!boardTile.isEmpty()) {
                // Position has existing tile
                if (boardTile.equals(String.valueOf(wordChar))) {
                    // Use board tile
                    positions.add(new BoardAnalyzerResponse.BoardPosition(row, startCol, boardTile, false));
                    connectsToExistingTile = true;
                } else {
                    // Board tile doesn't match
                    return null;
                }
            } else {
                // Need hand tile
                String neededTile = String.valueOf(wordChar);
                if (availableHandTiles.contains(neededTile)) {
                    availableHandTiles.remove(neededTile);
                    usedHandTiles.add(neededTile);
                    positions.add(new BoardAnalyzerResponse.BoardPosition(row, startCol, neededTile, true));
                    usedAtLeastOneHandTile = true;
                } else {
                    return null;
                }
            }
        }

        // Must use at least one hand tile to be a valid play
        if (!usedAtLeastOneHandTile) {
            return null;
        }

        // CRITICAL: Validate the complete word formed (including tiles before and after)
        // This prevents invalid words like "ATLOPHO" when placing "LOP" between "AT" and "PHO"
        String completeWord = extractCompleteWordFormedVertically(word, startRow, startCol, board);
        if (!completeWord.equals(word)) {
            // Word extends existing tiles - validate complete word is also valid
            if (!englishDictionaryService.isValidWord(completeWord)) {
                logger.debug("Rejecting '{}' at position ({},{}) - forms invalid complete word '{}'",
                    word, startRow, startCol, completeWord);
                return null;
            }
            logger.debug("Validated complete word '{}' formed by placing '{}' at position ({},{})",
                completeWord, word, startRow, startCol);
        }

        // Calculate score
        int score = calculateWordScore(word, positions, specialTiles);

        List<String> bonusesApplied = new ArrayList<>();
        if (scrabbleProperties.getSpecialTiles().getBoardAnalyzer().isEnabled()) {
            bonusesApplied.add("Special tiles considered");
        }

        return new BoardAnalyzerResponse.WordCombination(
            word, score, startRow, startCol, "VERTICAL",
            usedHandTiles, positions, bonusesApplied);
    }
    
    private int calculateWordScore(String word, List<BoardAnalyzerResponse.BoardPosition> positions, String[][] specialTiles) {
        int baseScore = 0;
        ScrabbleScoreUtil.SpecialTileMultiplier multiplierTracker = new ScrabbleScoreUtil.SpecialTileMultiplier();

        // Calculate base score with letter multipliers using centralized utility
        for (BoardAnalyzerResponse.BoardPosition pos : positions) {
            char letter = pos.getLetter().charAt(0);
            int letterScore = ScrabbleScoreUtil.getLetterScore(letter);

            // Apply special tile bonuses ONLY if:
            // 1. Special tiles are enabled
            // 2. This position uses a hand tile (newly placed)
            // 3. The special tile is not already "used" by an existing board tile
            if (scrabbleProperties.getSpecialTiles().getBoardAnalyzer().isEnabled() && pos.isUsesHandTile()) {
                String specialTileType = specialTiles[pos.getRow()][pos.getCol()];
                int originalLetterScore = letterScore;

                // Use centralized special tile logic (single source of truth)
                letterScore = ScrabbleScoreUtil.applySpecialTileMultiplier(letterScore, specialTileType, multiplierTracker);

                // Log what bonus was applied based on score change
                if (letterScore != originalLetterScore) {
                    if (letterScore == originalLetterScore * 2) {
                        logger.debug("Applied Double Letter bonus at {},{}: {} -> {}",
                                   pos.getRow(), pos.getCol(), originalLetterScore, letterScore);
                    } else if (letterScore == originalLetterScore * 3) {
                        logger.debug("Applied Triple Letter bonus at {},{}: {} -> {}",
                                   pos.getRow(), pos.getCol(), originalLetterScore, letterScore);
                    }
                } else if ("dw".equals(specialTileType) || "double_word".equals(specialTileType)) {
                    logger.debug("Applied Double Word bonus at {},{}", pos.getRow(), pos.getCol());
                } else if ("tw".equals(specialTileType) || "triple_word".equals(specialTileType)) {
                    logger.debug("Applied Triple Word bonus at {},{}", pos.getRow(), pos.getCol());
                }
            }

            baseScore += letterScore;
        }

        int finalScore = baseScore * multiplierTracker.getWordMultiplier();

        // Add 50-point bonus if all 7 tiles are used (bingo bonus)
        long handTilesUsed = positions.stream().filter(BoardAnalyzerResponse.BoardPosition::isUsesHandTile).count();
        if (handTilesUsed == 7) {
            finalScore += 50;
            logger.debug("Applied 50-point bingo bonus for using all 7 tiles");
        }

        logger.debug("Word: {}, Base: {}, Multiplier: {}, Final: {}", word, baseScore, multiplierTracker.getWordMultiplier(), finalScore);
        return finalScore;
    }
    
    
    /**
     * Extracts the complete word that would be formed by placing a word at a position horizontally.
     * This includes tiles BEFORE and AFTER the placed word if they're consecutive.
     *
     * Example:
     *   Board row 7: ["A", "T", "", "", "P", "H", "O", ...] (columns 0-6)
     *   Playing "LOP" at position (7, 2)
     *   Complete word formed: A-T-L-O-P-H-O = "ATLOPHO"
     *
     * @param placedWord The word being placed
     * @param row The row where the word is placed
     * @param startCol Starting column (0-indexed) where the word is placed
     * @param board Current state of the board
     * @return The complete word that would be formed (including prefix and suffix tiles)
     */
    private String extractCompleteWordFormedHorizontally(String placedWord, int row, int startCol, String[][] board) {
        StringBuilder completeWord = new StringBuilder();

        // Step 1: Scan backward from placement start to find prefix tiles
        int prefixStart = startCol;
        while (prefixStart > 0 && !board[row][prefixStart - 1].isEmpty()) {
            prefixStart--;
        }

        // Step 2: Add prefix tiles (tiles before the placement)
        for (int col = prefixStart; col < startCol; col++) {
            completeWord.append(board[row][col].toUpperCase());
        }

        // Step 3: Add the word being placed (mixing board tiles and new tiles)
        for (int i = 0; i < placedWord.length(); i++) {
            int col = startCol + i;
            if (col < board[0].length && !board[row][col].isEmpty()) {
                // Use existing board tile
                completeWord.append(board[row][col].toUpperCase());
            } else {
                // Use new tile from the word being placed
                completeWord.append(Character.toUpperCase(placedWord.charAt(i)));
            }
        }

        // Step 4: Scan forward from placement end to find suffix tiles
        int placementEnd = startCol + placedWord.length();
        int suffixPos = placementEnd;
        while (suffixPos < board[0].length && !board[row][suffixPos].isEmpty()) {
            completeWord.append(board[row][suffixPos].toUpperCase());
            suffixPos++;
        }

        return completeWord.toString();
    }

    /**
     * Extracts the complete word that would be formed by placing a word at a position vertically.
     * This includes tiles BEFORE and AFTER the placed word if they're consecutive.
     *
     * Example:
     *   Board column 7: ["A", "T", "", "", "P", "H", "O", ...] (rows 0-6)
     *   Playing "LOP" at position (2, 7)
     *   Complete word formed: A-T-L-O-P-H-O = "ATLOPHO"
     *
     * @param placedWord The word being placed
     * @param startRow Starting row (0-indexed) where the word is placed
     * @param col The column where the word is placed
     * @param board Current state of the board
     * @return The complete word that would be formed (including prefix and suffix tiles)
     */
    private String extractCompleteWordFormedVertically(String placedWord, int startRow, int col, String[][] board) {
        StringBuilder completeWord = new StringBuilder();

        // Step 1: Scan backward from placement start to find prefix tiles
        int prefixStart = startRow;
        while (prefixStart > 0 && !board[prefixStart - 1][col].isEmpty()) {
            prefixStart--;
        }

        // Step 2: Add prefix tiles (tiles before the placement)
        for (int row = prefixStart; row < startRow; row++) {
            completeWord.append(board[row][col].toUpperCase());
        }

        // Step 3: Add the word being placed (mixing board tiles and new tiles)
        for (int i = 0; i < placedWord.length(); i++) {
            int row = startRow + i;
            if (row < board.length && !board[row][col].isEmpty()) {
                // Use existing board tile
                completeWord.append(board[row][col].toUpperCase());
            } else {
                // Use new tile from the word being placed
                completeWord.append(Character.toUpperCase(placedWord.charAt(i)));
            }
        }

        // Step 4: Scan forward from placement end to find suffix tiles
        int placementEnd = startRow + placedWord.length();
        int suffixPos = placementEnd;
        while (suffixPos < board.length && !board[suffixPos][col].isEmpty()) {
            completeWord.append(board[suffixPos][col].toUpperCase());
            suffixPos++;
        }

        return completeWord.toString();
    }

    /**
     * Checks if a word placement connects to existing board tiles properly following Scrabble rules
     */
    private boolean wordConnectsToExistingTiles(String[][] board, String word, int startRow, int startCol,
                                              String direction, List<String> handLetters) {
        boolean connectsToExisting = false;
        boolean usesHandTile = false;
        boolean touchesExistingTile = false;
        List<String> availableHandTiles = new ArrayList<>(handLetters);
        
        for (int i = 0; i < word.length(); i++) {
            int row = startRow + (direction.equals("VERTICAL") ? i : 0);
            int col = startCol + (direction.equals("HORIZONTAL") ? i : 0);
            
            if (row >= board.length || col >= board[0].length) {
                return false;
            }
            
            char wordChar = word.charAt(i);
            String boardTile = board[row][col];
            
            if (!boardTile.isEmpty()) {
                if (boardTile.equals(String.valueOf(wordChar))) {
                    connectsToExisting = true;
                } else {
                    return false; // Board tile doesn't match word
                }
            } else {
                String neededTile = String.valueOf(wordChar);
                if (availableHandTiles.contains(neededTile)) {
                    availableHandTiles.remove(neededTile);
                    usesHandTile = true;
                    
                    // Check if this position touches an existing tile (adjacent positions)
                    if (touchesAdjacentTile(board, row, col)) {
                        touchesExistingTile = true;
                    }
                } else {
                    return false; // Don't have required hand tile
                }
            }
        }
        
        boolean hasExistingTiles = hasAnyTilesOnBoard(board);
        
        // Scrabble rules:
        // 1. Must use at least one hand tile
        // 2. If board is empty, word must pass through center (7,7 for 15x15)
        // 3. If board has tiles, must connect to or touch existing tiles
        if (!usesHandTile) {
            return false;
        }
        
        if (!hasExistingTiles) {
            // First word must pass through center square
            int center = board.length / 2;
            if (direction.equals("HORIZONTAL")) {
                return startRow == center && startCol <= center && (startCol + word.length() - 1) >= center;
            } else {
                return startCol == center && startRow <= center && (startRow + word.length() - 1) >= center;
            }
        } else {
            // Must connect to or touch existing tiles
            return connectsToExisting || touchesExistingTile;
        }
    }
    
    /**
     * Check if a position touches an adjacent tile (for proper Scrabble connections)
     */
    private boolean touchesAdjacentTile(String[][] board, int row, int col) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // up, down, left, right
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length) {
                if (!board[newRow][newCol].isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if the board has any existing tiles
     */
    private boolean hasAnyTilesOnBoard(String[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (!board[row][col].isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
    
}