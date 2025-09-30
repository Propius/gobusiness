package com.govtech.scrabble.service;

import com.govtech.scrabble.dto.WordFinderRequest;
import com.govtech.scrabble.dto.WordFinderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WordFinderScrabbleRulesTest {

    @Autowired
    private WordFinderService wordFinderService;

    @Test
    public void testPositionIndexingIssue() {
        // User reported: "P is on board position 5 but you are asking me to put A and S on position 2 and 3 to make ASP"
        // User says "position on board start from 1 but the word generate assumes board position starts at 0"
        
        System.out.println("=== TESTING POSITION INDEXING ===");
        
        WordFinderRequest request = new WordFinderRequest();
        
        // P at position 5 (using 0-based indexing, so position 4 in array)
        List<String> boardTiles = Arrays.asList(
            "", "", "", "", "P", "", "", "", "", "", "", "", "", "", ""
        );
        request.setBoardTiles(boardTiles);
        
        // Hand tiles that could potentially form ASP
        List<String> handTiles = Arrays.asList("A", "S");
        request.setHandTiles(handTiles);
        
        WordFinderResponse response = wordFinderService.findPossibleWords(request);
        
        System.out.println("Board layout: " + boardTiles);
        System.out.println("Hand tiles: " + handTiles);
        System.out.println("Found " + response.getPossibleWords().size() + " possible words:");
        
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("- Word: " + word.getWord());
            System.out.println("  Positions: " + word.getPositions() + " (1-based user display)");
            System.out.println("  Hand tiles used: " + word.getUsedHandTiles());
            System.out.println("  Board tiles used: " + word.getUsedBoardTiles());
            
            // Verify that if P is used from the board, it must be at correct position
            // P is at 0-based position 4, which should be displayed as 1-based position 5
            if (word.getUsedBoardTiles().contains("P")) {
                assertTrue(word.getPositions().contains(5), 
                    "If word uses board tile P, it must be at position 5 (1-based user display)");
            }
            
            // Check specific case: ASP should not be suggested with A,S at positions 3,4 if P is at position 5
            if (word.getWord().equals("ASP")) {
                List<Integer> positions = word.getPositions();
                System.out.println("  ASP word analysis:");
                System.out.println("    A at position: " + positions.get(0));
                System.out.println("    S at position: " + positions.get(1));
                System.out.println("    P at position: " + positions.get(2));
                
                // The word ASP should place A-S-P consecutively
                // If P is at board position 4 (0-based), then ASP should be at positions [3,4,5] (1-based display)
                if (word.getUsedBoardTiles().contains("P")) {
                    assertEquals(5, (int)positions.get(2), 
                        "P in word ASP must be at board position 5 (1-based)");
                    assertEquals(Arrays.asList(3, 4, 5), positions,
                        "ASP using board P at position 5 should be at positions [3,4,5] (1-based)");
                }
            }
        }
    }

    @Test
    public void testScrabbleRuleViolation() {
        // User reported: "i have A at position 1 and T at position 2 on the board by adding A and S on the board to make the word ASP will have the outcome of ATASP instead"
        
        System.out.println("\\n=== TESTING SCRABBLE RULE VIOLATION ===");
        
        WordFinderRequest request = new WordFinderRequest();
        
        // Board has A at position 1 (0-based: position 0) and T at position 2 (0-based: position 1)
        List<String> boardTiles = Arrays.asList(
            "A", "T", "", "", "", "", "", "", "", "", "", "", "", "", ""
        );
        request.setBoardTiles(boardTiles);
        
        // Hand tiles
        List<String> handTiles = Arrays.asList("S", "P");
        request.setHandTiles(handTiles);
        
        WordFinderResponse response = wordFinderService.findPossibleWords(request);
        
        System.out.println("Board layout: " + boardTiles);
        System.out.println("Hand tiles: " + handTiles);
        System.out.println("Found " + response.getPossibleWords().size() + " possible words:");
        
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("- Word: " + word.getWord());
            System.out.println("  Positions: " + word.getPositions() + " (1-based user display)");
            System.out.println("  Hand tiles used: " + word.getUsedHandTiles());
            System.out.println("  Board tiles used: " + word.getUsedBoardTiles());
            
            // Simulate what would happen if we place this word on the board
            char[] resultBoard = new char[15];
            for (int i = 0; i < boardTiles.size(); i++) {
                resultBoard[i] = boardTiles.get(i).isEmpty() ? '.' : boardTiles.get(i).charAt(0);
            }
            
            // Place the suggested word - convert 1-based positions back to 0-based for board array
            String wordStr = word.getWord();
            List<Integer> positions = word.getPositions();
            for (int i = 0; i < wordStr.length(); i++) {
                int pos = positions.get(i) - 1; // Convert from 1-based to 0-based
                if (pos >= 0 && pos < resultBoard.length) {
                    char existingChar = resultBoard[pos];
                    char newChar = wordStr.charAt(i);
                    
                    if (existingChar != '.' && existingChar != newChar) {
                        fail(String.format(
                            "Word '%s' violates Scrabble rules: position %d (0-based) has existing letter '%c' but word wants to place '%c'. " +
                            "This would result in invalid board state.",
                            wordStr, pos, existingChar, newChar));
                    }
                    
                    resultBoard[pos] = newChar;
                }
            }
            
            System.out.println("  Result board: " + String.valueOf(resultBoard));
            
            // Specifically check for ASP word placement
            if (word.getWord().equals("ASP")) {
                // ASP should not be placeable if it conflicts with existing A,T
                // If board has A at 0 and T at 1, then ASP cannot be placed at positions [1,2,3] (1-based)
                // because that would require replacing A with A (ok), T with S (NOT OK!)
                List<Integer> aspPositions = word.getPositions();
                if (aspPositions.equals(Arrays.asList(1, 2, 3))) {
                    fail("ASP at positions [1,2,3] (1-based) is invalid - it would require replacing board T at position 2 with S");
                }
            }
        }
    }

    @Test
    public void testValidWordPlacement() {
        System.out.println("\\n=== TESTING VALID WORD PLACEMENT ===");
        
        WordFinderRequest request = new WordFinderRequest();
        
        // Board has A at position 0, T at position 1  
        List<String> boardTiles = Arrays.asList(
            "A", "T", "", "", "", "", "", "", "", "", "", "", "", "", ""
        );
        request.setBoardTiles(boardTiles);
        
        // Hand tiles that can form valid words
        List<String> handTiles = Arrays.asList("S", "E", "R");
        request.setHandTiles(handTiles);
        
        WordFinderResponse response = wordFinderService.findPossibleWords(request);
        
        System.out.println("Board layout: " + boardTiles);
        System.out.println("Hand tiles: " + handTiles);
        System.out.println("Found " + response.getPossibleWords().size() + " possible words:");
        
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("- Word: " + word.getWord());
            System.out.println("  Positions: " + word.getPositions() + " (1-based user display)");
            System.out.println("  Hand tiles used: " + word.getUsedHandTiles());
            System.out.println("  Board tiles used: " + word.getUsedBoardTiles());
            
            // Validate that the word respects existing board tiles
            String wordStr = word.getWord();
            List<Integer> positions = word.getPositions();
            
            for (int i = 0; i < wordStr.length(); i++) {
                int boardPos = positions.get(i) - 1; // Convert from 1-based to 0-based
                char wordChar = wordStr.charAt(i);
                
                if (boardPos >= 0 && boardPos < boardTiles.size() && !boardTiles.get(boardPos).isEmpty()) {
                    char boardChar = boardTiles.get(boardPos).charAt(0);
                    assertEquals(boardChar, wordChar,
                        String.format("Word '%s' at position %d (1-based): word has '%c' but board has '%c'", 
                                    wordStr, positions.get(i), wordChar, boardChar));
                }
            }
        }
        
        // This test should pass - it validates that all suggested words properly use existing board tiles
    }
}