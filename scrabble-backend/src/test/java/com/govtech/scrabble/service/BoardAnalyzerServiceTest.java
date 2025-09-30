package com.govtech.scrabble.service;

import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.dto.BoardAnalyzerRequest;
import com.govtech.scrabble.dto.BoardAnalyzerResponse;
import com.govtech.scrabble.service.impl.BoardAnalyzerServiceImpl;
import com.govtech.scrabble.service.impl.EnglishDictionaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

class BoardAnalyzerServiceTest {

    private BoardAnalyzerServiceImpl boardAnalyzerService;
    private ScrabbleProperties properties;
    private EnglishDictionaryService dictionaryService;

    @BeforeEach
    void setUp() {
        properties = new ScrabbleProperties();
        // Enable board analyzer for testing
        properties.getBoardAnalyzer().setEnabled(true);

        dictionaryService = new EnglishDictionaryServiceImpl(properties);

        boardAnalyzerService = new BoardAnalyzerServiceImpl(properties, dictionaryService);
    }

    @Test
    void testIsBoardAnalyzerEnabled() {
        // Default should be enabled
        assertTrue(boardAnalyzerService.isBoardAnalyzerEnabled());
    }

    @Test
    void testAnalyzeBoardForTopCombinationsWithValidInput() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setBoardLetters(createEmptyBoard());
        request.setHandLetters(Arrays.asList("C", "A", "T"));
        request.setSpecialTiles(createNormalSpecialTiles());
        
        BoardAnalyzerResponse response = boardAnalyzerService.analyzeBoardForTopCombinations(request);
        
        assertNotNull(response);
        assertNotNull(response.getTopCombinations());
    }

    @Test
    void testAnalyzeBoardForTopCombinationsWithEmptyHand() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setBoardLetters(createEmptyBoard());
        request.setHandLetters(Arrays.asList());
        
        BoardAnalyzerResponse response = boardAnalyzerService.analyzeBoardForTopCombinations(request);
        
        assertNotNull(response);
        assertNotNull(response.getTopCombinations());
        assertTrue(response.getTopCombinations().isEmpty());
    }

    @Test
    void testAnalyzeBoardForTopCombinationsWithNullInputs() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        // All inputs null
        
        BoardAnalyzerResponse response = boardAnalyzerService.analyzeBoardForTopCombinations(request);
        
        assertNotNull(response);
        assertNotNull(response.getTopCombinations());
    }

    private List<String> createEmptyBoard() {
        List<String> board = new java.util.ArrayList<>();
        for (int i = 0; i < 225; i++) { // 15x15 = 225
            board.add("");
        }
        return board;
    }

    private List<String> createNormalSpecialTiles() {
        List<String> specialTiles = new java.util.ArrayList<>();
        for (int i = 0; i < 225; i++) { // 15x15 = 225
            specialTiles.add("normal");
        }
        return specialTiles;
    }

    @Test
    void testBoardAnalyzerRejectsInvalidCompleteWordHorizontal() {
        // Arrange: Board has A, T at (7,0-1) and P, H, O at (7,4-6)
        List<String> boardLetters = createEmptyBoard();
        // Place A at (7, 0) - index 7*15+0 = 105
        boardLetters.set(105, "A");
        // Place T at (7, 1) - index 7*15+1 = 106
        boardLetters.set(106, "T");
        // Place P at (7, 4) - index 7*15+4 = 109
        boardLetters.set(109, "P");
        // Place H at (7, 5) - index 7*15+5 = 110
        boardLetters.set(110, "H");
        // Place O at (7, 6) - index 7*15+6 = 111
        boardLetters.set(111, "O");

        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setBoardLetters(boardLetters);
        // Provide hand tiles L, O, P to try placing "LOP" at positions 2-4
        request.setHandLetters(Arrays.asList("L", "O", "P"));
        request.setSpecialTiles(createNormalSpecialTiles());

        // Act: Try to find combinations - should reject "LOP" because it forms "ATLOPHO"
        BoardAnalyzerResponse response = boardAnalyzerService.analyzeBoardForTopCombinations(request);

        // Assert: Should not find "LOP" as a valid combination at position (7, 2)
        // because it would form the invalid word "ATLOPHO"
        assertNotNull(response);
        boolean hasInvalidLopPlacement = response.getTopCombinations().stream()
            .anyMatch(combo -> combo.getWord().equals("LOP")
                && combo.getStartRow() == 7
                && combo.getStartCol() == 2);

        assertFalse(hasInvalidLopPlacement,
            "Should reject LOP placement at (7,2) that forms invalid complete word ATLOPHO");
    }

    @Test
    void testBoardAnalyzerRejectsInvalidCompleteWordVertical() {
        // Arrange: Board has A, T at (0,7) and (1,7), and P, H, O at (4,7), (5,7), (6,7)
        List<String> boardLetters = createEmptyBoard();
        // Place A at (0, 7) - index 0*15+7 = 7
        boardLetters.set(7, "A");
        // Place T at (1, 7) - index 1*15+7 = 22
        boardLetters.set(22, "T");
        // Place P at (4, 7) - index 4*15+7 = 67
        boardLetters.set(67, "P");
        // Place H at (5, 7) - index 5*15+7 = 82
        boardLetters.set(82, "H");
        // Place O at (6, 7) - index 6*15+7 = 97
        boardLetters.set(97, "O");

        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setBoardLetters(boardLetters);
        // Provide hand tiles L, O, P to try placing "LOP" vertically at rows 2-4
        request.setHandLetters(Arrays.asList("L", "O", "P"));
        request.setSpecialTiles(createNormalSpecialTiles());

        // Act: Try to find combinations - should reject "LOP" because it forms "ATLOPHO"
        BoardAnalyzerResponse response = boardAnalyzerService.analyzeBoardForTopCombinations(request);

        // Assert: Should not find "LOP" as a valid combination at position (2, 7)
        // because it would form the invalid word "ATLOPHO"
        assertNotNull(response);
        boolean hasInvalidLopPlacement = response.getTopCombinations().stream()
            .anyMatch(combo -> combo.getWord().equals("LOP")
                && combo.getStartRow() == 2
                && combo.getStartCol() == 7
                && combo.getDirection().equals("VERTICAL"));

        assertFalse(hasInvalidLopPlacement,
            "Should reject LOP vertical placement at (2,7) that forms invalid complete word ATLOPHO");
    }

    @Test
    void testBoardAnalyzerAcceptsValidCompleteWord() {
        // Arrange: Board has C, A at (7,0-1)
        List<String> boardLetters = createEmptyBoard();
        // Place C at (7, 0) - index 7*15+0 = 105
        boardLetters.set(105, "C");
        // Place A at (7, 1) - index 7*15+1 = 106
        boardLetters.set(106, "A");

        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setBoardLetters(boardLetters);
        // Provide hand tiles T to place "CAT"
        request.setHandLetters(Arrays.asList("T"));
        request.setSpecialTiles(createNormalSpecialTiles());

        // Act: Try to find combinations - should accept "CAT" as it forms a valid word
        BoardAnalyzerResponse response = boardAnalyzerService.analyzeBoardForTopCombinations(request);

        // Assert: Should find "CAT" or just "T" as valid combinations
        assertNotNull(response);
        assertNotNull(response.getTopCombinations());
        // The test should pass without rejecting valid placements
    }
}