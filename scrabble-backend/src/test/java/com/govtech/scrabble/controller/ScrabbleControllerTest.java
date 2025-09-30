package com.govtech.scrabble.controller;

import com.govtech.scrabble.controller.impl.ScrabbleControllerImpl;
import com.govtech.scrabble.dto.CalculateScoreRequest;
import com.govtech.scrabble.dto.CalculateScoreResponse;
import com.govtech.scrabble.dto.ScoreRequest;
import com.govtech.scrabble.dto.ScoreResponse;
import com.govtech.scrabble.dto.WordFinderRequest;
import com.govtech.scrabble.dto.WordFinderResponse;
import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.service.ScrabbleService;
import com.govtech.scrabble.service.WordFinderService;
import com.govtech.scrabble.service.InputValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrabbleControllerTest {

    @Mock
    private ScrabbleService scrabbleService;

    @Mock
    private WordFinderService wordFinderService;

    @Mock
    private InputValidationService inputValidationService;

    @Mock
    private ScrabbleProperties scrabbleProperties;

    @Mock
    private ScrabbleProperties.WordFinder wordFinderConfig;

    @InjectMocks
    private ScrabbleControllerImpl scrabbleController;


    @Test
    void testCalculateScore() {
        CalculateScoreResponse mockResponse = new CalculateScoreResponse("TEST", 4);
        when(scrabbleService.calculateScore(eq("TEST"))).thenReturn(mockResponse);

        // Mock validation service to do nothing (valid input)
        doNothing().when(inputValidationService).validateWord(anyString());
        doNothing().when(inputValidationService).validatePositions(any());
        doNothing().when(inputValidationService).validateSpecialTiles(any());
        doNothing().when(inputValidationService).validatePositionsAndTilesMatch(any(), any());

        ResponseEntity<CalculateScoreResponse> response = scrabbleController.calculateScore("test", null, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("TEST", response.getBody().getWord());
        assertEquals(4, response.getBody().getTotalScore());
        verify(scrabbleService, times(1)).calculateScore("TEST");
    }

    @Test
    void testSaveScore() {
        ScoreRequest request = new ScoreRequest("TEST");
        ScoreResponse mockResponse = new ScoreResponse();
        mockResponse.setWord("TEST");
        mockResponse.setPoints(4);
        mockResponse.setId(1L);
        mockResponse.setCreatedAt(LocalDateTime.now());

        when(scrabbleService.saveScore(eq("TEST"))).thenReturn(mockResponse);

        ResponseEntity<ScoreResponse> response = scrabbleController.saveScore(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("TEST", response.getBody().getWord());
        assertEquals(4, response.getBody().getPoints());
        assertEquals(1L, response.getBody().getId());
        verify(scrabbleService, times(1)).saveScore("TEST");
    }

    @Test
    void testGetTopScores() {
        ScoreResponse score1 = new ScoreResponse();
        score1.setWord("QUIZ");
        score1.setPoints(22);
        score1.setId(1L);
        
        ScoreResponse score2 = new ScoreResponse();
        score2.setWord("TEST");
        score2.setPoints(4);
        score2.setId(2L);

        List<ScoreResponse> mockScores = Arrays.asList(score1, score2);
        when(scrabbleService.getTopScores()).thenReturn(mockScores);

        ResponseEntity<List<ScoreResponse>> response = scrabbleController.getTopScores();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
        assertEquals("QUIZ", response.getBody().get(0).getWord());
        assertEquals(22, response.getBody().get(0).getPoints());
        verify(scrabbleService, times(1)).getTopScores();
    }

    @Test
    void testSaveScore_InvalidWord_ThrowsException() {
        ScoreRequest request = new ScoreRequest("INVALID");
        when(scrabbleService.saveScore(eq("INVALID"))).thenThrow(new IllegalArgumentException("Invalid word"));

        // Controller doesn't catch exceptions - they bubble up to global exception handler
        assertThrows(IllegalArgumentException.class, () -> {
            scrabbleController.saveScore(request);
        });

        verify(scrabbleService, times(1)).saveScore("INVALID");
    }

    @Test
    void testFindPossibleWords_FeatureDisabled() {
        WordFinderRequest request = new WordFinderRequest();
        when(scrabbleProperties.getWordFinder()).thenReturn(wordFinderConfig);
        when(wordFinderConfig.isEnabled()).thenReturn(false);

        // Controller throws IllegalStateException when feature is disabled
        assertThrows(IllegalStateException.class, () -> {
            scrabbleController.findPossibleWords(request);
        });

        verify(wordFinderService, never()).findPossibleWords(any());
    }

    @Test
    void testFindPossibleWords_FeatureEnabled() {
        WordFinderRequest request = new WordFinderRequest();
        WordFinderResponse mockResponse = new WordFinderResponse(Arrays.asList(), 0, "No words found");

        when(scrabbleProperties.getWordFinder()).thenReturn(wordFinderConfig);
        when(wordFinderConfig.isEnabled()).thenReturn(true);
        when(wordFinderService.findPossibleWords(request)).thenReturn(mockResponse);

        ResponseEntity<WordFinderResponse> response = scrabbleController.findPossibleWords(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("No words found", response.getBody().getMessage());
        verify(wordFinderService, times(1)).findPossibleWords(request);
    }

    @Test
    void testCalculateScore_WithInvalidWord_ValidationException() {
        doThrow(new IllegalArgumentException("Word cannot be empty"))
                .when(inputValidationService).validateWord("");

        assertThrows(IllegalArgumentException.class, () -> {
            scrabbleController.calculateScore("", null, null);
        });

        verify(inputValidationService, times(1)).validateWord("");
        verify(scrabbleService, never()).calculateScore(anyString());
    }

    @Test
    void testCalculateScore_WithNullWord_ValidationException() {
        doThrow(new IllegalArgumentException("Word cannot be null"))
                .when(inputValidationService).validateWord(null);

        assertThrows(IllegalArgumentException.class, () -> {
            scrabbleController.calculateScore(null, null, null);
        });

        verify(inputValidationService, times(1)).validateWord(null);
        verify(scrabbleService, never()).calculateScore(anyString());
    }

    @Test
    void testCalculateScore_WithInvalidPositions_ValidationException() {
        String word = "TEST";
        List<Integer> invalidPositions = Arrays.asList(-1, 2, 3, 4);

        doNothing().when(inputValidationService).validateWord(anyString());
        doThrow(new IllegalArgumentException("Positions cannot contain negative values"))
                .when(inputValidationService).validatePositions(invalidPositions);

        assertThrows(IllegalArgumentException.class, () -> {
            scrabbleController.calculateScore(word, invalidPositions, null);
        });

        verify(inputValidationService, times(1)).validatePositions(invalidPositions);
        verify(scrabbleService, never()).calculateScore(anyString());
    }

    @Test
    void testCalculateScore_WithInvalidSpecialTiles_ValidationException() {
        String word = "TEST";
        List<String> invalidTiles = Arrays.asList("invalid");

        doNothing().when(inputValidationService).validateWord(anyString());
        doNothing().when(inputValidationService).validatePositions(any());
        doThrow(new IllegalArgumentException("Invalid special tile type"))
                .when(inputValidationService).validateSpecialTiles(invalidTiles);

        assertThrows(IllegalArgumentException.class, () -> {
            scrabbleController.calculateScore(word, null, invalidTiles);
        });

        verify(inputValidationService, times(1)).validateSpecialTiles(invalidTiles);
        verify(scrabbleService, never()).calculateScore(anyString());
    }

    @Test
    void testCalculateScore_WithMismatchedPositionsAndTiles_ValidationException() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("dl", "normal");

        doNothing().when(inputValidationService).validateWord(anyString());
        doNothing().when(inputValidationService).validatePositions(any());
        doNothing().when(inputValidationService).validateSpecialTiles(any());
        doThrow(new IllegalArgumentException("Positions and special tiles must have same size"))
                .when(inputValidationService).validatePositionsAndTilesMatch(positions, specialTiles);

        assertThrows(IllegalArgumentException.class, () -> {
            scrabbleController.calculateScore(word, positions, specialTiles);
        });

        verify(inputValidationService, times(1)).validatePositionsAndTilesMatch(positions, specialTiles);
        verify(scrabbleService, never()).calculateScore(anyString());
    }

    @Test
    void testCalculateScore_WithSpecialTiles_FeatureEnabled() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("dl", "normal", "tw", "normal");

        ScrabbleProperties.SpecialTiles specialTilesConfig = mock(ScrabbleProperties.SpecialTiles.class);
        ScrabbleProperties.SpecialTiles.FeatureFlag scoreCalculatorConfig = mock(ScrabbleProperties.SpecialTiles.FeatureFlag.class);

        CalculateScoreResponse mockResponse = new CalculateScoreResponse("TEST", 20);

        doNothing().when(inputValidationService).validateWord(anyString());
        doNothing().when(inputValidationService).validatePositions(any());
        doNothing().when(inputValidationService).validateSpecialTiles(any());
        doNothing().when(inputValidationService).validatePositionsAndTilesMatch(any(), any());
        when(scrabbleProperties.getSpecialTiles()).thenReturn(specialTilesConfig);
        when(specialTilesConfig.getScoreCalculator()).thenReturn(scoreCalculatorConfig);
        when(scoreCalculatorConfig.isEnabled()).thenReturn(true);
        when(scrabbleService.calculateScoreWithSpecialTiles(any())).thenReturn(mockResponse);

        ResponseEntity<CalculateScoreResponse> response = scrabbleController.calculateScore(word, positions, specialTiles);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("TEST", response.getBody().getWord());
        assertEquals(20, response.getBody().getTotalScore());
        verify(scrabbleService, times(1)).calculateScoreWithSpecialTiles(any());
        verify(scrabbleService, never()).calculateScore(anyString());
    }

    @Test
    void testCalculateScore_WithSpecialTiles_FeatureDisabled() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("dl", "normal", "tw", "normal");

        ScrabbleProperties.SpecialTiles specialTilesConfig = mock(ScrabbleProperties.SpecialTiles.class);
        ScrabbleProperties.SpecialTiles.FeatureFlag scoreCalculatorConfig = mock(ScrabbleProperties.SpecialTiles.FeatureFlag.class);

        CalculateScoreResponse mockResponse = new CalculateScoreResponse("TEST", 4);

        doNothing().when(inputValidationService).validateWord(anyString());
        doNothing().when(inputValidationService).validatePositions(any());
        doNothing().when(inputValidationService).validateSpecialTiles(any());
        doNothing().when(inputValidationService).validatePositionsAndTilesMatch(any(), any());
        when(scrabbleProperties.getSpecialTiles()).thenReturn(specialTilesConfig);
        when(specialTilesConfig.getScoreCalculator()).thenReturn(scoreCalculatorConfig);
        when(scoreCalculatorConfig.isEnabled()).thenReturn(false);
        when(scrabbleService.calculateScore("TEST")).thenReturn(mockResponse);

        ResponseEntity<CalculateScoreResponse> response = scrabbleController.calculateScore(word, positions, specialTiles);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("TEST", response.getBody().getWord());
        assertEquals(4, response.getBody().getTotalScore());
        verify(scrabbleService, times(1)).calculateScore("TEST");
        verify(scrabbleService, never()).calculateScoreWithSpecialTiles(any());
    }

    @Test
    void testCalculateScoreWithSpecialTiles_RequestBody_Success() {
        CalculateScoreRequest request = new CalculateScoreRequest();
        request.setWord("QUIZ");
        request.setPositions(Arrays.asList(0, 1, 2, 3));
        request.setSpecialTiles(Arrays.asList("tw", "normal", "normal", "normal"));

        ScrabbleProperties.SpecialTiles specialTilesConfig = mock(ScrabbleProperties.SpecialTiles.class);
        ScrabbleProperties.SpecialTiles.FeatureFlag scoreCalculatorConfig = mock(ScrabbleProperties.SpecialTiles.FeatureFlag.class);

        CalculateScoreResponse mockResponse = new CalculateScoreResponse("QUIZ", 66);

        when(scrabbleProperties.getSpecialTiles()).thenReturn(specialTilesConfig);
        when(specialTilesConfig.getScoreCalculator()).thenReturn(scoreCalculatorConfig);
        when(scoreCalculatorConfig.isEnabled()).thenReturn(true);
        when(scrabbleService.calculateScoreWithSpecialTiles(request)).thenReturn(mockResponse);

        ResponseEntity<CalculateScoreResponse> response = scrabbleController.calculateScoreWithSpecialTiles(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("QUIZ", response.getBody().getWord());
        assertEquals(66, response.getBody().getTotalScore());
        verify(scrabbleService, times(1)).calculateScoreWithSpecialTiles(request);
    }

    @Test
    void testCalculateScoreWithSpecialTiles_RequestBody_FeatureDisabled() {
        CalculateScoreRequest request = new CalculateScoreRequest();
        request.setWord("QUIZ");

        ScrabbleProperties.SpecialTiles specialTilesConfig = mock(ScrabbleProperties.SpecialTiles.class);
        ScrabbleProperties.SpecialTiles.FeatureFlag scoreCalculatorConfig = mock(ScrabbleProperties.SpecialTiles.FeatureFlag.class);

        when(scrabbleProperties.getSpecialTiles()).thenReturn(specialTilesConfig);
        when(specialTilesConfig.getScoreCalculator()).thenReturn(scoreCalculatorConfig);
        when(scoreCalculatorConfig.isEnabled()).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> {
            scrabbleController.calculateScoreWithSpecialTiles(request);
        });

        verify(scrabbleService, never()).calculateScoreWithSpecialTiles(any());
    }

    @Test
    void testSaveScore_Success() {
        ScoreRequest request = new ScoreRequest("HELLO");
        ScoreResponse mockResponse = new ScoreResponse();
        mockResponse.setWord("HELLO");
        mockResponse.setPoints(8);
        mockResponse.setId(5L);
        mockResponse.setCreatedAt(LocalDateTime.now());

        when(scrabbleService.saveScore("HELLO")).thenReturn(mockResponse);

        ResponseEntity<ScoreResponse> response = scrabbleController.saveScore(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("HELLO", response.getBody().getWord());
        assertEquals(8, response.getBody().getPoints());
        assertEquals(5L, response.getBody().getId());
        verify(scrabbleService, times(1)).saveScore("HELLO");
    }

    @Test
    void testGetTopScores_EmptyList() {
        List<ScoreResponse> emptyList = Arrays.asList();
        when(scrabbleService.getTopScores()).thenReturn(emptyList);

        ResponseEntity<List<ScoreResponse>> response = scrabbleController.getTopScores();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().size());
        verify(scrabbleService, times(1)).getTopScores();
    }
}