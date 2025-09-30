package com.govtech.scrabble.service.impl;

import com.govtech.scrabble.dto.WordFinderRequest;
import com.govtech.scrabble.dto.WordFinderResponse;
import com.govtech.scrabble.service.EnglishDictionaryService;
import com.govtech.scrabble.service.WordFinderService;
import com.govtech.scrabble.util.ScrabbleScoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordFinderServiceImpl implements WordFinderService {

    private static final Logger logger = LoggerFactory.getLogger(WordFinderServiceImpl.class);

    private final EnglishDictionaryService englishDictionaryService;

    public WordFinderServiceImpl(EnglishDictionaryService englishDictionaryService) {
        this.englishDictionaryService = englishDictionaryService;
    }
    
    public WordFinderResponse findPossibleWords(WordFinderRequest request) {
        logger.info("Finding possible words for board tiles: {} and hand tiles: {}", 
                    request.getBoardTiles(), request.getHandTiles());
        
        List<String> boardTiles = request.getBoardTiles() != null ? request.getBoardTiles() : new ArrayList<>();
        List<String> handTiles = request.getHandTiles() != null ? request.getHandTiles() : new ArrayList<>();
        
        // Normalize inputs
        boardTiles = boardTiles.stream()
                .map(tile -> tile != null ? tile.toUpperCase().trim() : "")
                .collect(Collectors.toList());
        
        handTiles = handTiles.stream()
                .map(tile -> tile != null ? tile.toUpperCase().trim() : "")
                .filter(tile -> !tile.isEmpty())
                .collect(Collectors.toList());
        
        if (handTiles.isEmpty()) {
            return new WordFinderResponse(new ArrayList<>(), 0, "No hand tiles provided");
        }
        
        // Combine all available letters for dictionary search
        List<String> allAvailableLetters = new ArrayList<>(handTiles);
        
        // Add non-empty board tiles as available letters
        boardTiles.stream()
            .filter(tile -> !tile.isEmpty())
            .forEach(allAvailableLetters::add);
        
        if (allAvailableLetters.isEmpty()) {
            return new WordFinderResponse(new ArrayList<>(), 0, "No tiles available");
        }
        
        // Use external dictionary service to find possible words
        int minLength = 3; // Minimum word length
        int maxLength = Math.min(15, allAvailableLetters.size()); // Maximum reasonable word length

        logger.debug("Finding words with available letters: {} (min: {}, max: {})",
                   allAvailableLetters, minLength, maxLength);

        List<String> dictionaryWords = englishDictionaryService.findPossibleWords(
            allAvailableLetters, minLength, maxLength);

        logger.debug("Dictionary returned {} potential words", dictionaryWords.size());

        List<WordFinderResponse.PossibleWord> possibleWords = new ArrayList<>();

        // Find words that can be formed with board/hand tile constraints
        for (String word : dictionaryWords) {
            // Additional validation: ensure word is actually valid in dictionary
            if (!englishDictionaryService.isValidWord(word)) {
                logger.debug("Skipping invalid dictionary word: '{}'", word);
                continue;
            }

            List<WordFinderResponse.PossibleWord> wordMatches = findWordMatches(word, boardTiles, handTiles);
            possibleWords.addAll(wordMatches);
        }
        
        // Sort by score (descending) and then by word length (descending)
        possibleWords.sort((w1, w2) -> {
            int scoreCompare = Integer.compare(w2.getScore(), w1.getScore());
            if (scoreCompare != 0) return scoreCompare;
            return Integer.compare(w2.getWord().length(), w1.getWord().length());
        });
        
        // Convert positions to 1-based for user display and limit results to top 10
        List<WordFinderResponse.PossibleWord> topWords = possibleWords.stream()
                .limit(10)
                .map(this::convertToUserFriendlyPositions)
                .collect(Collectors.toList());
        
        String message = possibleWords.isEmpty() ? 
                "No words found with the available tiles" :
                String.format("Found %d possible words (showing top %d)", 
                             possibleWords.size(), topWords.size());
        
        return new WordFinderResponse(topWords, possibleWords.size(), message);
    }
    
    private List<WordFinderResponse.PossibleWord> findWordMatches(String word, List<String> boardTiles, List<String> handTiles) {
        List<WordFinderResponse.PossibleWord> matches = new ArrayList<>();

        // Find board tile sets (consecutive non-empty tiles)
        List<BoardTileSet> boardTileSets = findBoardTileSets(boardTiles);

        // Try to place the word at different positions, ensuring it connects to at least one board tile set
        for (int startPos = 0; startPos <= Math.max(0, boardTiles.size() - word.length()); startPos++) {
            WordFinderResponse.PossibleWord match = tryPlaceWord(word, startPos, boardTiles, handTiles, boardTileSets);
            if (match != null) {
                matches.add(match);
            }
        }

        return matches;
    }

    /**
     * Extracts the complete word that would be formed by placing a word at a position.
     * This includes tiles BEFORE and AFTER the placed word if they're consecutive.
     *
     * Example:
     *   Board: ["A", "T", "", "", "P", "H", "O", ...] (positions 0-6)
     *   Playing "LOP" at position 2
     *   Complete word formed: A-T-L-O-P-H-O = "ATLOPHO"
     *
     * @param placedWord The word being placed
     * @param startPos Starting position (0-indexed) where the word is placed
     * @param boardTiles Current state of the board tiles
     * @return The complete word that would be formed (including prefix and suffix tiles)
     */
    private String extractCompleteWordFormed(String placedWord, int startPos, List<String> boardTiles) {
        StringBuilder completeWord = new StringBuilder();

        // Step 1: Scan backward from placement start to find prefix tiles
        int prefixStart = startPos;
        while (prefixStart > 0 && !boardTiles.get(prefixStart - 1).isEmpty()) {
            prefixStart--;
        }

        // Step 2: Add prefix tiles (tiles before the placement)
        for (int i = prefixStart; i < startPos; i++) {
            completeWord.append(boardTiles.get(i).toUpperCase());
        }

        // Step 3: Add the word being placed (mixing board tiles and new tiles)
        for (int i = 0; i < placedWord.length(); i++) {
            int boardPos = startPos + i;
            if (boardPos < boardTiles.size() && !boardTiles.get(boardPos).isEmpty()) {
                // Use existing board tile
                completeWord.append(boardTiles.get(boardPos).toUpperCase());
            } else {
                // Use new tile from the word being placed
                completeWord.append(Character.toUpperCase(placedWord.charAt(i)));
            }
        }

        // Step 4: Scan forward from placement end to find suffix tiles
        int placementEnd = startPos + placedWord.length();
        int suffixPos = placementEnd;
        while (suffixPos < boardTiles.size() && !boardTiles.get(suffixPos).isEmpty()) {
            completeWord.append(boardTiles.get(suffixPos).toUpperCase());
            suffixPos++;
        }

        return completeWord.toString();
    }
    
    private WordFinderResponse.PossibleWord tryPlaceWord(String word, int startPos, List<String> boardTiles,
                                                       List<String> handTiles, List<BoardTileSet> boardTileSets) {
        List<String> availableHandTiles = new ArrayList<>(handTiles);
        List<String> usedHandTiles = new ArrayList<>();
        List<String> usedBoardTiles = new ArrayList<>();
        List<Integer> positions = new ArrayList<>();
        boolean usedAtLeastOneHandTile = false;
        boolean connectsToExistingTileSet = false;

        // Check if word placement would connect to at least one existing board tile set
        int wordStart = startPos;
        int wordEnd = startPos + word.length() - 1;

        // Validate that word doesn't go out of bounds
        if (wordEnd >= boardTiles.size()) {
            return null;
        }

        for (BoardTileSet tileSet : boardTileSets) {
            if (wordOverlapsOrConnectsToSet(wordStart, wordEnd, tileSet)) {
                connectsToExistingTileSet = true;
                break;
            }
        }

        // If there are board tiles but word doesn't connect to any set, skip
        if (!boardTileSets.isEmpty() && !connectsToExistingTileSet) {
            return null;
        }

        // First pass: verify all existing board tiles match the word exactly
        for (int i = 0; i < word.length(); i++) {
            char wordChar = word.charAt(i);
            int boardPos = startPos + i;

            // Check if this position has a board tile
            if (boardPos < boardTiles.size() && !boardTiles.get(boardPos).isEmpty()) {
                String boardTile = boardTiles.get(boardPos).toUpperCase();
                String expectedChar = String.valueOf(wordChar).toUpperCase();
                if (!boardTile.equals(expectedChar)) {
                    // Board tile doesn't match word letter - invalid placement
                    logger.debug("Word '{}' cannot be placed at position {} - board has '{}' but word needs '{}'",
                               word, boardPos, boardTile, expectedChar);
                    return null;
                }
            }
        }

        // CRITICAL: Validate the complete word formed (including tiles before and after)
        // This prevents invalid words like "ATLOPHO" when placing "LOP" between "AT" and "PHO"
        String completeWord = extractCompleteWordFormed(word, startPos, boardTiles);
        if (!completeWord.equals(word)) {
            // Word extends existing tiles - validate complete word is also valid
            if (!englishDictionaryService.isValidWord(completeWord)) {
                logger.debug("Rejecting '{}' at position {} - forms invalid complete word '{}'",
                    word, startPos, completeWord);
                return null;
            }
            logger.debug("Validated complete word '{}' formed by placing '{}' at position {}",
                completeWord, word, startPos);
        }
        
        // Second pass: build the word placement if first pass succeeded
        for (int i = 0; i < word.length(); i++) {
            char wordChar = word.charAt(i);
            int boardPos = startPos + i;
            positions.add(boardPos);
            
            // Check if this position has a board tile
            if (boardPos < boardTiles.size() && !boardTiles.get(boardPos).isEmpty()) {
                String boardTile = boardTiles.get(boardPos).toUpperCase();
                // Use existing board tile - we already verified it matches
                usedBoardTiles.add(boardTile);
            } else {
                // Position is empty, need to use a hand tile
                String neededTile = String.valueOf(wordChar).toUpperCase();
                if (availableHandTiles.contains(neededTile)) {
                    availableHandTiles.remove(neededTile);
                    usedHandTiles.add(neededTile);
                    usedAtLeastOneHandTile = true;
                } else {
                    // Don't have the required hand tile
                    logger.debug("Word '{}' cannot be placed - missing hand tile '{}'", word, neededTile);
                    return null;
                }
            }
        }
        
        // Must use at least one hand tile to be a valid play
        if (!usedAtLeastOneHandTile) {
            logger.debug("Word '{}' rejected - no hand tiles used", word);
            return null;
        }
        
        // Must connect to existing board tiles if any exist
        if (!boardTileSets.isEmpty() && usedBoardTiles.isEmpty()) {
            logger.debug("Word '{}' rejected - doesn't connect to existing board tiles", word);
            return null;
        }
        
        // Additional Scrabble rule validation: if placing a word that uses existing board tiles,
        // ensure the word placement makes logical sense (consecutive tiles)
        if (!usedBoardTiles.isEmpty()) {
            // Validate that the word forms a proper consecutive sequence
            // and doesn't create invalid intersections with existing tiles
            if (!validateScrabbleWordPlacement(word, startPos, boardTiles)) {
                logger.debug("Word '{}' rejected - violates Scrabble placement rules", word);
                return null;
            }
        }
        
        // Calculate score
        int score = calculateWordScore(word);
        
        logger.debug("Valid word placement found: '{}' at positions {} (startPos={}) using hand tiles {} and board tiles {}",
                   word, positions, startPos, usedHandTiles, usedBoardTiles);
        
        return new WordFinderResponse.PossibleWord(
                word, score, positions, usedHandTiles, usedBoardTiles);
    }
    
    private int calculateWordScore(String word) {
        return ScrabbleScoreUtil.calculateWordScore(word);
    }
    
    /**
     * Converts internal 0-based positions to user-friendly 1-based positions
     */
    private WordFinderResponse.PossibleWord convertToUserFriendlyPositions(WordFinderResponse.PossibleWord word) {
        // Convert 0-based positions to 1-based for user display
        List<Integer> userPositions = word.getPositions().stream()
                .map(pos -> pos + 1)
                .collect(Collectors.toList());
        
        return new WordFinderResponse.PossibleWord(
            word.getWord(), 
            word.getScore(), 
            userPositions, 
            word.getUsedHandTiles(), 
            word.getUsedBoardTiles()
        );
    }
    
    /**
     * Validates that a word placement follows Scrabble rules for using existing board tiles.
     * Key rules:
     * 1. Existing board tiles must be used consecutively (no gaps)
     * 2. All existing tiles in the word range must match the word characters
     * 3. The word must form a valid consecutive sequence with existing tiles
     */
    private boolean validateScrabbleWordPlacement(String word, int startPos, List<String> boardTiles) {
        // Collect information about existing tiles in the word placement range
        List<Integer> existingTilePositions = new ArrayList<>();
        List<Character> existingTileChars = new ArrayList<>();

        // First pass: identify all existing board tiles in the placement range
        for (int i = 0; i < word.length(); i++) {
            int boardPos = startPos + i;
            char wordChar = word.charAt(i);

            // If this position has an existing board tile
            if (boardPos < boardTiles.size() && !boardTiles.get(boardPos).isEmpty()) {
                String boardTile = boardTiles.get(boardPos).toUpperCase();
                String expectedChar = String.valueOf(wordChar).toUpperCase();

                // Board tile must match the word character exactly
                if (!boardTile.equals(expectedChar)) {
                    logger.debug("Scrabble rule violation: position {} has '{}' but word '{}' needs '{}'",
                               boardPos, boardTile, word, expectedChar);
                    return false;
                }

                existingTilePositions.add(boardPos);
                existingTileChars.add(boardTile.charAt(0));
            }
        }

        // If there are existing tiles, validate they form a consecutive sequence
        if (!existingTilePositions.isEmpty()) {
            // Check that existing tiles are consecutive (no gaps between them)
            for (int i = 1; i < existingTilePositions.size(); i++) {
                int prevPos = existingTilePositions.get(i - 1);
                int currPos = existingTilePositions.get(i);

                // Check if there's a gap (more than 1 position apart)
                if (currPos - prevPos > 1) {
                    // There's a gap - check if the word uses positions in between
                    // This is allowed only if we're filling the gap with hand tiles
                    boolean hasHandTileInGap = false;
                    for (int gapPos = prevPos + 1; gapPos < currPos; gapPos++) {
                        int wordIndex = gapPos - startPos;
                        if (wordIndex >= 0 && wordIndex < word.length()) {
                            hasHandTileInGap = true;
                            break;
                        }
                    }

                    if (!hasHandTileInGap) {
                        logger.debug("Scrabble rule violation: word '{}' has non-consecutive existing tiles (gap between positions {} and {})",
                                   word, prevPos, currPos);
                        return false;
                    }
                }
            }

            // Validate that existing tiles form a substring of the word
            String existingSequence = existingTileChars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());

            // Find where in the word the existing tiles should appear
            int firstExistingPos = existingTilePositions.get(0);
            int wordStartIndex = firstExistingPos - startPos;

            // Extract the corresponding substring from the word
            StringBuilder wordSubstring = new StringBuilder();
            for (int pos : existingTilePositions) {
                int wordIndex = pos - startPos;
                if (wordIndex >= 0 && wordIndex < word.length()) {
                    wordSubstring.append(word.charAt(wordIndex));
                }
            }

            // The extracted characters must match the existing tiles exactly
            if (!existingSequence.equalsIgnoreCase(wordSubstring.toString())) {
                logger.debug("Scrabble rule violation: existing tiles '{}' don't match word substring '{}'",
                           existingSequence, wordSubstring);
                return false;
            }

            // Additional validation: ensure existing tiles appear consecutively in the WORD itself
            // For example, if board has P-H-O at positions 2-3-4, and we're placing "HOSE" at position 3,
            // the word "HOSE" uses H-O but not P, which means it's skipping P (invalid)

            // Get the range of board tiles
            int minExistingPos = existingTilePositions.get(0);
            int maxExistingPos = existingTilePositions.get(existingTilePositions.size() - 1);

            // Critical validation: Check for "tile skipping" within consecutive board tile sets
            // Rule: If a word uses tiles from a consecutive board tile set, it cannot skip
            // any tiles from that set that fall within the word's placement range.
            //
            // Example: Board has P-H-O at positions 2-3-4 (consecutive set)
            //   - PHONE at position 2: uses P-H-O consecutively ✅ VALID
            //   - HOSE at position 3: uses H-O but skips P ❌ INVALID (P is before word start)
            //   - THOSE at position 2: uses only H-O, skipping P in between ❌ INVALID

            for (BoardTileSet tileSet : findBoardTileSets(boardTiles)) {
                int wordEndPos = startPos + word.length() - 1;

                // Check if word's range overlaps with this tile set
                boolean wordOverlapsSet = (startPos <= tileSet.end && wordEndPos >= tileSet.start);

                if (!wordOverlapsSet) {
                    continue; // No overlap, skip this tile set
                }

                // Word overlaps with this tile set
                // Calculate the range of overlap
                int overlapStart = Math.max(startPos, tileSet.start);
                int overlapEnd = Math.min(wordEndPos, tileSet.end);

                // Count how many tiles from this set the word uses
                int tilesUsedFromSet = 0;
                for (int pos = overlapStart; pos <= overlapEnd; pos++) {
                    if (existingTilePositions.contains(pos)) {
                        tilesUsedFromSet++;
                    }
                }

                // Calculate expected number of tiles in overlap range
                int tilesInOverlapRange = overlapEnd - overlapStart + 1;

                // If word uses ANY tiles from this set in the overlap range, it must use ALL of them
                if (tilesUsedFromSet > 0 && tilesUsedFromSet < tilesInOverlapRange) {
                    logger.debug("Scrabble rule violation: word '{}' at position {} overlaps tile set [{}-{}] " +
                               "(overlap range [{}-{}]) but only uses {} out of {} tiles (skipping tiles)",
                               word, startPos, tileSet.start, tileSet.end,
                               overlapStart, overlapEnd, tilesUsedFromSet, tilesInOverlapRange);
                    return false;
                }

                // Additional check: If word uses tiles from this set, but starts AFTER the set starts,
                // it means we're skipping the beginning of the set (like HOSE skipping P in P-H-O)
                if (tilesUsedFromSet > 0 && startPos > tileSet.start && startPos <= tileSet.end) {
                    logger.debug("Scrabble rule violation: word '{}' at position {} skips beginning of tile set [{}-{}] " +
                               "(set starts at {} but word starts at {})",
                               word, startPos, tileSet.start, tileSet.end, tileSet.start, startPos);
                    return false;
                }
            }
        }

        return true;
    }
    
    /**
     * Finds consecutive sets of board tiles (non-empty tiles that are adjacent)
     */
    private List<BoardTileSet> findBoardTileSets(List<String> boardTiles) {
        List<BoardTileSet> tileSets = new ArrayList<>();
        int currentSetStart = -1;
        
        for (int i = 0; i < boardTiles.size(); i++) {
            boolean hasLetter = i < boardTiles.size() && !boardTiles.get(i).isEmpty();
            
            if (hasLetter && currentSetStart == -1) {
                // Starting a new set
                currentSetStart = i;
            } else if (!hasLetter && currentSetStart != -1) {
                // Ending current set
                tileSets.add(new BoardTileSet(currentSetStart, i - 1));
                currentSetStart = -1;
            }
        }
        
        // Handle set that ends at the last position
        if (currentSetStart != -1) {
            tileSets.add(new BoardTileSet(currentSetStart, boardTiles.size() - 1));
        }
        
        return tileSets;
    }
    
    /**
     * Checks if a word placement overlaps or connects to an existing tile set
     */
    private boolean wordOverlapsOrConnectsToSet(int wordStart, int wordEnd, BoardTileSet tileSet) {
        // Word overlaps with tile set
        if ((wordStart <= tileSet.end && wordEnd >= tileSet.start)) {
            return true;
        }
        
        // Word connects adjacent to tile set (no gap)
        if (wordEnd == tileSet.start - 1 || wordStart == tileSet.end + 1) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Represents a set of consecutive board tiles
     */
    private static class BoardTileSet {
        final int start;
        final int end;
        
        BoardTileSet(int start, int end) {
            this.start = start;
            this.end = end;
        }
        
        @Override
        public String toString() {
            return String.format("BoardTileSet[%d-%d]", start, end);
        }
    }
}