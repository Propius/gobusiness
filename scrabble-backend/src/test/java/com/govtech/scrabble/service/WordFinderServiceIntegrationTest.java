package com.govtech.scrabble.service;

import com.govtech.scrabble.dto.WordFinderRequest;
import com.govtech.scrabble.dto.WordFinderResponse;
import com.govtech.scrabble.service.impl.WordFinderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WordFinderServiceIntegrationTest {

    @Autowired
    private WordFinderService wordFinderService;

    @Test
    public void testWordFinderRespectsExistingBoardTiles() {
        // Test case: board has P at position 2, H at position 3, O at position 4
        // Hand has other letters
        // Should NOT suggest words like "OLD" that would require replacing board letters
        
        WordFinderRequest request = new WordFinderRequest();
        
        // Create board tiles with P-H-O at positions 2-3-4
        List<String> boardTiles = Arrays.asList(
            "", "", "P", "H", "O", "", "", "", "", "", "", "", "", "", ""
        );
        request.setBoardTiles(boardTiles);
        
        // Hand tiles that could form other words
        List<String> handTiles = Arrays.asList("L", "D", "E", "R", "S");
        request.setHandTiles(handTiles);
        
        WordFinderResponse response = wordFinderService.findPossibleWords(request);
        
        assertNotNull(response);
        assertNotNull(response.getPossibleWords());
        
        // Verify that no suggested words violate the existing board layout
        for (WordFinderResponse.PossibleWord possibleWord : response.getPossibleWords()) {
            List<Integer> positions = possibleWord.getPositions();
            String word = possibleWord.getWord();
            
            // Check each position where the word would be placed
            for (int i = 0; i < word.length(); i++) {
                int boardPosition = positions.get(i);
                char wordChar = word.charAt(i);
                
                // Convert 1-based position back to 0-based for array access
                int arrayIndex = boardPosition - 1;
                
                // If there's already a letter on the board at this position
                if (arrayIndex >= 0 && arrayIndex < boardTiles.size() && 
                    !boardTiles.get(arrayIndex).isEmpty()) {
                    
                    String boardLetter = boardTiles.get(arrayIndex);
                    
                    // The word character must match the existing board letter
                    assertEquals(boardLetter.toUpperCase(), 
                               String.valueOf(wordChar).toUpperCase(),
                               String.format("Word '%s' at position %d (0-based: %d) requires '%c' but board has '%s'", 
                                           word, boardPosition, arrayIndex, wordChar, boardLetter));
                }
            }
        }
        
        // Additional check: ensure no invalid words are suggested
        for (WordFinderResponse.PossibleWord possibleWord : response.getPossibleWords()) {
            String word = possibleWord.getWord();
            
            // Words like "OLD" should not be suggested when board has P-H-O at positions 2-3-4
            // because "OLD" at any position would either:
            // 1. Not use the existing board letters P-H-O, or  
            // 2. Try to replace them with O-L-D
            
            if (word.equals("OLD")) {
                List<Integer> positions = possibleWord.getPositions();
                
                // Convert to 1-based positions for the check (P at position 3, H at position 4, O at position 5 in 1-based)
                boolean conflictsWithP = positions.contains(3); // P is at index 2, which is position 3 in 1-based
                boolean conflictsWithH = positions.contains(4); // H is at index 3, which is position 4 in 1-based  
                boolean conflictsWithO = positions.contains(5); // O is at index 4, which is position 5 in 1-based
                
                // If the word "OLD" is suggested, it should not conflict with existing letters
                // This test will fail if the word finder incorrectly suggests "OLD"
                assertFalse(conflictsWithP || conflictsWithH || conflictsWithO,
                          "Word 'OLD' should not be suggested at positions that conflict with existing P-H-O letters");
            }
        }
        
        System.out.println("Found " + response.getPossibleWords().size() + " valid words:");
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("- " + word.getWord() + " at positions " + word.getPositions() + 
                             " using hand tiles " + word.getUsedHandTiles() + 
                             " and board tiles " + word.getUsedBoardTiles());
        }
    }

    @Test
    public void testWordFinderWithEmptyBoard() {
        // Test with completely empty board - should work normally
        WordFinderRequest request = new WordFinderRequest();

        List<String> boardTiles = Arrays.asList(
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
        );
        request.setBoardTiles(boardTiles);

        List<String> handTiles = Arrays.asList("C", "A", "T", "S");
        request.setHandTiles(handTiles);

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        // Integration test: verify service runs without error
        // Word finding depends on LanguageTool which may vary by environment
        assertNotNull(response);
        assertNotNull(response.getPossibleWords());
        assertNotNull(response.getMessage());
    }

    @Test
    public void testWordFinderMustUseHandTiles() {
        // Test that word finder only suggests words using available hand tiles
        WordFinderRequest request = new WordFinderRequest();
        
        List<String> boardTiles = Arrays.asList(
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
        );
        request.setBoardTiles(boardTiles);
        
        // Only provide specific hand tiles
        List<String> handTiles = Arrays.asList("D", "O", "G");
        request.setHandTiles(handTiles);
        
        WordFinderResponse response = wordFinderService.findPossibleWords(request);
        
        assertNotNull(response);
        
        // Every suggested word should only use letters D, O, G
        for (WordFinderResponse.PossibleWord possibleWord : response.getPossibleWords()) {
            String word = possibleWord.getWord();
            for (char c : word.toCharArray()) {
                assertTrue(handTiles.contains(String.valueOf(c)) || 
                          handTiles.stream().anyMatch(tile -> tile.equalsIgnoreCase(String.valueOf(c))),
                          "Word '" + word + "' uses letter '" + c + "' which is not in hand tiles " + handTiles);
            }
        }
    }
}