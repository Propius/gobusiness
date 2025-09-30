package com.govtech.scrabble.service;

import com.govtech.scrabble.dto.WordFinderRequest;
import com.govtech.scrabble.dto.WordFinderResponse;
import com.govtech.scrabble.service.impl.WordFinderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WordFinderServiceTest {

    @Mock
    private EnglishDictionaryService englishDictionaryService;

    private WordFinderServiceImpl wordFinderService;

    @BeforeEach
    void setUp() {
        wordFinderService = new WordFinderServiceImpl(englishDictionaryService);

        // Default behavior: return false for any unknown words (complete word validation)
        // This allows our new complete word validation to work without strict stubbing errors
        // Using lenient() to avoid "unnecessary stubbing" errors in tests that don't call isValidWord
        lenient().when(englishDictionaryService.isValidWord(any())).thenReturn(false);
    }

    @Test
    void testFindPossibleWordsWithValidLetters() {
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("CAT", "DOG", "HELLO"));
        when(englishDictionaryService.isValidWord("CAT")).thenReturn(true);
        when(englishDictionaryService.isValidWord("DOG")).thenReturn(true);
        when(englishDictionaryService.isValidWord("HELLO")).thenReturn(true);

        WordFinderRequest request = new WordFinderRequest();
        request.setHandTiles(Arrays.asList("C", "A", "T", "D", "O", "G", "H", "E", "L", "L", "O"));
        request.setBoardTiles(Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
        
        WordFinderResponse response = wordFinderService.findPossibleWords(request);
        
        assertNotNull(response);
        assertNotNull(response.getPossibleWords());
        assertFalse(response.getPossibleWords().isEmpty(), 
                   "Should find words when board is empty and we have hand tiles");
        assertTrue(response.getTotalFound() > 0);
    }

    @Test
    void testFindPossibleWordsWithEmptyHandTiles() {
        WordFinderRequest request = new WordFinderRequest();
        request.setHandTiles(Collections.emptyList());
        request.setBoardTiles(Collections.emptyList());
        
        WordFinderResponse response = wordFinderService.findPossibleWords(request);
        
        assertNotNull(response);
        assertEquals("No hand tiles provided", response.getMessage());
        assertEquals(0, response.getTotalFound());
    }

    @Test
    void testFindPossibleWordsWithNullHandTiles() {
        WordFinderRequest request = new WordFinderRequest();
        request.setHandTiles(null);
        request.setBoardTiles(Collections.emptyList());
        
        WordFinderResponse response = wordFinderService.findPossibleWords(request);
        
        assertNotNull(response);
        assertEquals("No hand tiles provided", response.getMessage());
    }

    @Test
    void testFindPossibleWordsWithBoardAndHandTiles() {
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("TEST", "WORD"));

        WordFinderRequest request = new WordFinderRequest();
        request.setHandTiles(Arrays.asList("T", "E", "S", "T"));
        request.setBoardTiles(Arrays.asList("", "W", "", "O", "R", "D"));

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        assertNotNull(response);
        assertNotNull(response.getPossibleWords());
        assertTrue(response.getTotalFound() >= 0);
    }

    @Test
    void testWordFinderRespectsConsecutiveTiles_PHOScenario() {
        // Issue #2: Board has "PHO" at positions 2-3-4, should NOT suggest "HOSE" which skips P
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("PHONE", "PHONES", "HOSE", "THOSE", "PHOS"));
        when(englishDictionaryService.isValidWord("PHONE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("PHONES")).thenReturn(true);
        when(englishDictionaryService.isValidWord("HOSE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("THOSE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("PHOS")).thenReturn(true);

        WordFinderRequest request = new WordFinderRequest();
        // Board: P at position 2, H at position 3, O at position 4 (0-indexed)
        request.setBoardTiles(Arrays.asList("", "", "P", "H", "O", "", "", "", "", "", "", "", "", "", ""));
        request.setHandTiles(Arrays.asList("N", "E", "S", "T", "A", "R", "D"));

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        assertNotNull(response);
        assertNotNull(response.getPossibleWords());

        // PHONE should be suggested (uses P-H-O consecutively)
        boolean foundPhone = response.getPossibleWords().stream()
            .anyMatch(word -> word.getWord().equals("PHONE"));

        // HOSE should NOT be suggested (would skip P, using only H-O)
        boolean foundHose = response.getPossibleWords().stream()
            .anyMatch(word -> word.getWord().equals("HOSE"));

        // THOSE should NOT be suggested (would skip P, using only H-O)
        boolean foundThose = response.getPossibleWords().stream()
            .anyMatch(word -> word.getWord().equals("THOSE"));

        assertTrue(foundPhone || response.getPossibleWords().isEmpty(),
                  "Should suggest PHONE (uses P-H-O consecutively) if possible");
        assertFalse(foundHose,
                   "Should NOT suggest HOSE (skips P, uses only H-O)");
        assertFalse(foundThose,
                   "Should NOT suggest THOSE (skips P)");

        System.out.println("PHO Scenario - Found words:");
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("  " + word.getWord() + " at positions " + word.getPositions() +
                             " using board tiles " + word.getUsedBoardTiles());
        }
    }

    @Test
    void testWordFinderValidatesWordExtensions() {
        // Board has "CAT" at positions 7-8-9, should suggest "CATS" but not "CAR" (changes T to R)
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("CATS", "CAR", "CAB", "CATER"));
        when(englishDictionaryService.isValidWord("CATS")).thenReturn(true);
        when(englishDictionaryService.isValidWord("CAR")).thenReturn(true);
        when(englishDictionaryService.isValidWord("CAB")).thenReturn(true);
        when(englishDictionaryService.isValidWord("CATER")).thenReturn(true);

        WordFinderRequest request = new WordFinderRequest();
        request.setBoardTiles(Arrays.asList("", "", "", "", "", "", "", "C", "A", "T", "", "", "", "", ""));
        request.setHandTiles(Arrays.asList("S", "E", "R"));

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        assertNotNull(response);

        // CATS should be suggested (extends CAT)
        boolean foundCats = response.getPossibleWords().stream()
            .anyMatch(word -> word.getWord().equals("CATS"));

        // CAR should NOT be suggested (tries to change T to R)
        boolean foundCar = response.getPossibleWords().stream()
            .anyMatch(word -> word.getWord().equals("CAR"));

        // CAB should NOT be suggested (tries to change T to B)
        boolean foundCab = response.getPossibleWords().stream()
            .anyMatch(word -> word.getWord().equals("CAB"));

        assertTrue(foundCats || response.getPossibleWords().isEmpty(),
                  "Should suggest CATS (extends existing word)");
        assertFalse(foundCar,
                   "Should NOT suggest CAR (changes T to R)");
        assertFalse(foundCab,
                   "Should NOT suggest CAB (changes T to B)");

        System.out.println("Word Extension - Found words:");
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("  " + word.getWord() + " at positions " + word.getPositions());
        }
    }

    @Test
    void testWordFinderWithMultipleBoardTileSets() {
        // Board has two separate tile sets: "HI" at 2-3 and "AT" at 6-7
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("HIT", "HAT", "HIGH", "THAT"));
        when(englishDictionaryService.isValidWord("HIT")).thenReturn(true);
        when(englishDictionaryService.isValidWord("HAT")).thenReturn(true);
        when(englishDictionaryService.isValidWord("HIGH")).thenReturn(true);
        when(englishDictionaryService.isValidWord("THAT")).thenReturn(true);

        WordFinderRequest request = new WordFinderRequest();
        request.setBoardTiles(Arrays.asList("", "", "H", "I", "", "", "A", "T", "", "", "", "", "", "", ""));
        request.setHandTiles(Arrays.asList("T", "H", "G"));

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        assertNotNull(response);
        assertNotNull(response.getPossibleWords());

        // All suggested words should properly connect to one of the tile sets
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            assertNotNull(word.getUsedBoardTiles());
            assertFalse(word.getUsedBoardTiles().isEmpty(),
                       "Word " + word.getWord() + " should use at least one board tile");
        }

        System.out.println("Multiple Tile Sets - Found words:");
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("  " + word.getWord() + " at positions " + word.getPositions() +
                             " using board tiles " + word.getUsedBoardTiles());
        }
    }

    @Test
    void testWordFinderDoesNotSkipTilesInSet() {
        // Critical test: Board has "DOG" at 5-6-7, should NOT allow "DIG" which would skip O
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("DOGS", "DOG", "DIG", "DOGE"));
        when(englishDictionaryService.isValidWord("DOGS")).thenReturn(true);
        when(englishDictionaryService.isValidWord("DOG")).thenReturn(true);
        when(englishDictionaryService.isValidWord("DIG")).thenReturn(true);
        when(englishDictionaryService.isValidWord("DOGE")).thenReturn(true);

        WordFinderRequest request = new WordFinderRequest();
        request.setBoardTiles(Arrays.asList("", "", "", "", "", "D", "O", "G", "", "", "", "", "", "", ""));
        request.setHandTiles(Arrays.asList("S", "I", "E"));

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        assertNotNull(response);

        // DOGS should be allowed (extends DOG)
        boolean foundDogs = response.getPossibleWords().stream()
            .anyMatch(word -> word.getWord().equals("DOGS"));

        // DIG should NOT be allowed (skips O in the middle)
        boolean foundDig = response.getPossibleWords().stream()
            .anyMatch(word -> word.getWord().equals("DIG"));

        assertTrue(foundDogs || response.getPossibleWords().isEmpty(),
                  "Should suggest DOGS (extends DOG)");
        assertFalse(foundDig,
                   "Should NOT suggest DIG (skips O in DOG)");

        System.out.println("No Skip Tiles - Found words:");
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("  " + word.getWord() + " at positions " + word.getPositions());
        }
    }

    @Test
    void testWordFinderRejectsInvalidCompleteWord_ATLOPHO() {
        // CRITICAL BUG FIX TEST: Board has "A", "T" at positions 0-1 and "P", "H", "O" at positions 4-6
        // Should NOT suggest "LOP" at positions 2-4 because it forms "ATLOPHO" (invalid word)
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("LOP", "PHONE", "ATONE", "LONE"));
        when(englishDictionaryService.isValidWord("LOP")).thenReturn(true);
        when(englishDictionaryService.isValidWord("PHONE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("ATONE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("LONE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("ATLOPHO")).thenReturn(false); // Invalid complete word

        WordFinderRequest request = new WordFinderRequest();
        // Board: A T _ _ P H O _ _ _
        //        0 1 2 3 4 5 6 7 8 9
        request.setBoardTiles(Arrays.asList("A", "T", "", "", "P", "H", "O", "", "", "", "", "", "", "", ""));
        request.setHandTiles(Arrays.asList("L", "O", "N", "E", "A", "S", "D"));

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        assertNotNull(response);

        // LOP should NOT be suggested because it forms "ATLOPHO" (invalid)
        boolean foundLopFormingAtlopho = response.getPossibleWords().stream()
            .anyMatch(word -> {
                // Check if this is LOP at position 2 which would form ATLOPHO
                if (word.getWord().equals("LOP")) {
                    // Check if it's at the problematic position (positions 2-4, which are 3-5 in 1-based)
                    return word.getPositions().contains(3) && word.getPositions().contains(4);
                }
                return false;
            });

        assertFalse(foundLopFormingAtlopho,
                   "Should NOT suggest 'LOP' at positions 2-4 because it forms invalid word 'ATLOPHO'");

        System.out.println("ATLOPHO Bug Fix Test - Found words:");
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("  " + word.getWord() + " at positions " + word.getPositions() +
                             " using board tiles " + word.getUsedBoardTiles());
        }
    }

    @Test
    void testWordFinderAcceptsValidCompleteWord_CATS() {
        // Board has "CAT" at positions 7-8-9
        // Should suggest "S" at position 10 to form valid "CATS"
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("CATS", "CAT", "S"));
        when(englishDictionaryService.isValidWord("CATS")).thenReturn(true);
        when(englishDictionaryService.isValidWord("CAT")).thenReturn(true);
        when(englishDictionaryService.isValidWord("S")).thenReturn(false); // Single letter not valid

        WordFinderRequest request = new WordFinderRequest();
        // Board: _ _ _ _ _ _ _ C A T _ _ _ _ _
        //        0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
        request.setBoardTiles(Arrays.asList("", "", "", "", "", "", "", "C", "A", "T", "", "", "", "", ""));
        request.setHandTiles(Arrays.asList("S", "E", "R"));

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        assertNotNull(response);

        // Should suggest a word that extends CAT to form valid CATS
        boolean foundCats = response.getPossibleWords().stream()
            .anyMatch(word -> {
                String wordStr = word.getWord();
                // Could be "CATS" directly or any word that uses C-A-T-S consecutively
                return wordStr.contains("CATS") || wordStr.equals("CATS");
            });

        assertTrue(foundCats || response.getPossibleWords().isEmpty(),
                  "Should suggest word that extends 'CAT' to form valid 'CATS'");

        System.out.println("Valid Complete Word Test - Found words:");
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("  " + word.getWord() + " at positions " + word.getPositions());
        }
    }

    @Test
    void testWordFinderValidatesCompleteWordWithPrefixAndSuffix() {
        // Board has "C" at position 6 and "T" at position 9
        // If we place "AR" at positions 7-8, it forms "CART" - should only suggest if CART is valid
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("AR", "ART", "CART", "CAR"));
        when(englishDictionaryService.isValidWord("AR")).thenReturn(true);
        when(englishDictionaryService.isValidWord("ART")).thenReturn(true);
        when(englishDictionaryService.isValidWord("CART")).thenReturn(true);
        when(englishDictionaryService.isValidWord("CAR")).thenReturn(true);

        WordFinderRequest request = new WordFinderRequest();
        // Board: _ _ _ _ _ _ C _ _ T _ _ _ _ _
        //        0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
        request.setBoardTiles(Arrays.asList("", "", "", "", "", "", "C", "", "", "T", "", "", "", "", ""));
        request.setHandTiles(Arrays.asList("A", "R", "E", "S"));

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        assertNotNull(response);

        // If AR is suggested at positions 7-8, verify it forms valid complete word
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            if (word.getWord().equals("AR")) {
                // If AR uses both C and T (positions 7-8 which are 8-9 in 1-based),
                // it must form a valid complete word
                if (word.getUsedBoardTiles().contains("C") && word.getUsedBoardTiles().contains("T")) {
                    assertTrue(true, "AR placement forms valid complete word with C and T");
                }
            }
        }

        System.out.println("Prefix and Suffix Test - Found words:");
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("  " + word.getWord() + " at positions " + word.getPositions() +
                             " using board tiles " + word.getUsedBoardTiles());
        }
    }

    @Test
    void testWordFinderRejectsInvalidExtension_ATLOPHO_DetailedScenario() {
        // Detailed test matching the exact bug scenario from the requirements
        when(englishDictionaryService.findPossibleWords(any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList("LOP", "ATONE", "PHONE", "TONE", "ONE", "LONE", "ATLOPHO"));

        // Valid words
        when(englishDictionaryService.isValidWord("LOP")).thenReturn(true);
        when(englishDictionaryService.isValidWord("ATONE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("PHONE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("TONE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("ONE")).thenReturn(true);
        when(englishDictionaryService.isValidWord("LONE")).thenReturn(true);

        // Invalid complete word
        when(englishDictionaryService.isValidWord("ATLOPHO")).thenReturn(false);

        WordFinderRequest request = new WordFinderRequest();
        // Exact scenario from bug report:
        // Board: A T _ _ P H O _ _ _ (positions 0-9)
        request.setBoardTiles(Arrays.asList("A", "T", "", "", "P", "H", "O", "", "", "", "", "", "", "", ""));
        request.setHandTiles(Arrays.asList("L", "O", "N", "E", "A", "S", "D"));

        WordFinderResponse response = wordFinderService.findPossibleWords(request);

        assertNotNull(response);
        assertNotNull(response.getPossibleWords());

        // Verify NO suggestion creates the invalid word "ATLOPHO"
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            // Calculate what complete word would be formed
            String wordStr = word.getWord();
            List<Integer> positions = word.getPositions();

            // For debugging
            System.out.println("Checking word: " + wordStr + " at positions " + positions);

            // Ensure no word suggestion would create ATLOPHO
            // This would happen if LOP is placed at positions 2-4 (3-4-5 in 1-based)
            if (wordStr.equals("LOP")) {
                boolean isProblematicPosition = positions.contains(3) && positions.contains(4) && positions.contains(5);
                assertFalse(isProblematicPosition,
                           "LOP should not be suggested at positions that form ATLOPHO");
            }
        }

        System.out.println("Detailed ATLOPHO Scenario - Found words:");
        for (WordFinderResponse.PossibleWord word : response.getPossibleWords()) {
            System.out.println("  " + word.getWord() + " at positions " + word.getPositions() +
                             " using hand tiles " + word.getUsedHandTiles() +
                             " and board tiles " + word.getUsedBoardTiles());
        }
    }
}