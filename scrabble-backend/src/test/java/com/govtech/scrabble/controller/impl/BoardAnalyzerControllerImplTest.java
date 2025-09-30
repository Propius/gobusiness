package com.govtech.scrabble.controller.impl;

import com.govtech.scrabble.dto.BoardAnalyzerRequest;
import com.govtech.scrabble.dto.BoardAnalyzerResponse;
import com.govtech.scrabble.service.BoardAnalyzerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardAnalyzerControllerImplTest {

    @Mock
    private BoardAnalyzerService boardAnalyzerService;

    @InjectMocks
    private BoardAnalyzerControllerImpl boardAnalyzerController;

    @Test
    void testAnalyzeBoard_Success() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setHandLetters(List.of("A", "B", "C"));

        BoardAnalyzerResponse mockResponse = new BoardAnalyzerResponse();
        mockResponse.setTopCombinations(new ArrayList<>());
        mockResponse.setTotalCombinationsCount(0);
        mockResponse.setMessage("Board analysis completed successfully");

        when(boardAnalyzerService.analyzeBoardForTopCombinations(request)).thenReturn(mockResponse);

        ResponseEntity<BoardAnalyzerResponse> response = boardAnalyzerController.analyzeBoard(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Board analysis completed successfully", response.getBody().getMessage());
        assertEquals(0, response.getBody().getTotalCombinationsCount());
        verify(boardAnalyzerService, times(1)).analyzeBoardForTopCombinations(request);
    }

    @Test
    void testAnalyzeBoard_FeatureDisabled_Returns503() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setHandLetters(List.of("A", "B", "C"));

        when(boardAnalyzerService.analyzeBoardForTopCombinations(request))
                .thenThrow(new IllegalStateException("Board analyzer feature is disabled"));

        ResponseEntity<BoardAnalyzerResponse> response = boardAnalyzerController.analyzeBoard(request);

        assertEquals(503, response.getStatusCode().value());
        verify(boardAnalyzerService, times(1)).analyzeBoardForTopCombinations(request);
    }

    @Test
    void testAnalyzeBoard_ValidationError_Returns400() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();

        when(boardAnalyzerService.analyzeBoardForTopCombinations(request))
                .thenThrow(new IllegalArgumentException("Hand tiles cannot be empty"));

        ResponseEntity<BoardAnalyzerResponse> response = boardAnalyzerController.analyzeBoard(request);

        assertEquals(400, response.getStatusCode().value());
        verify(boardAnalyzerService, times(1)).analyzeBoardForTopCombinations(request);
    }

    @Test
    void testAnalyzeBoard_NullPointerException_Returns400() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();

        when(boardAnalyzerService.analyzeBoardForTopCombinations(request))
                .thenThrow(new NullPointerException("Board data is null"));

        ResponseEntity<BoardAnalyzerResponse> response = boardAnalyzerController.analyzeBoard(request);

        assertEquals(400, response.getStatusCode().value());
        verify(boardAnalyzerService, times(1)).analyzeBoardForTopCombinations(request);
    }

    @Test
    void testAnalyzeBoard_WithTopCombinations_ReturnsValidResponse() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setHandLetters(List.of("Q", "U", "I", "Z"));

        BoardAnalyzerResponse.WordCombination combination1 = new BoardAnalyzerResponse.WordCombination();
        combination1.setWord("QUIZ");
        combination1.setTotalScore(50);

        List<BoardAnalyzerResponse.WordCombination> combinations = List.of(combination1);

        BoardAnalyzerResponse mockResponse = new BoardAnalyzerResponse();
        mockResponse.setTopCombinations(combinations);
        mockResponse.setTotalCombinationsCount(1);
        mockResponse.setMessage("Found 1 possible word placement");

        when(boardAnalyzerService.analyzeBoardForTopCombinations(request)).thenReturn(mockResponse);

        ResponseEntity<BoardAnalyzerResponse> response = boardAnalyzerController.analyzeBoard(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTopCombinations().size());
        assertEquals("QUIZ", response.getBody().getTopCombinations().get(0).getWord());
        assertEquals(50, response.getBody().getTopCombinations().get(0).getTotalScore());
        verify(boardAnalyzerService, times(1)).analyzeBoardForTopCombinations(request);
    }

    @Test
    void testAnalyzeBoard_EmptyHandTiles_Returns400() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setHandLetters(List.of());

        when(boardAnalyzerService.analyzeBoardForTopCombinations(request))
                .thenThrow(new IllegalArgumentException("Hand tiles cannot be empty"));

        ResponseEntity<BoardAnalyzerResponse> response = boardAnalyzerController.analyzeBoard(request);

        assertEquals(400, response.getStatusCode().value());
        verify(boardAnalyzerService, times(1)).analyzeBoardForTopCombinations(request);
    }

    @Test
    void testGetStatus_FeatureEnabled() {
        when(boardAnalyzerService.isBoardAnalyzerEnabled()).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = boardAnalyzerController.getStatus();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("enabled"));
        assertEquals("board-analyzer", response.getBody().get("feature"));
        assertEquals("Full board analysis for optimal word placement", response.getBody().get("description"));
        verify(boardAnalyzerService, times(1)).isBoardAnalyzerEnabled();
    }

    @Test
    void testGetStatus_FeatureDisabled() {
        when(boardAnalyzerService.isBoardAnalyzerEnabled()).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = boardAnalyzerController.getStatus();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse((Boolean) response.getBody().get("enabled"));
        assertEquals("board-analyzer", response.getBody().get("feature"));
        verify(boardAnalyzerService, times(1)).isBoardAnalyzerEnabled();
    }

    @Test
    void testGetStatus_ResponseFormat() {
        when(boardAnalyzerService.isBoardAnalyzerEnabled()).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = boardAnalyzerController.getStatus();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertTrue(response.getBody().containsKey("enabled"));
        assertTrue(response.getBody().containsKey("feature"));
        assertTrue(response.getBody().containsKey("description"));
        verify(boardAnalyzerService, times(1)).isBoardAnalyzerEnabled();
    }

    @Test
    void testAnalyzeBoard_RuntimeException_Returns400() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setHandLetters(List.of("X", "Y", "Z"));

        when(boardAnalyzerService.analyzeBoardForTopCombinations(request))
                .thenThrow(new RuntimeException("Unexpected error during board analysis"));

        ResponseEntity<BoardAnalyzerResponse> response = boardAnalyzerController.analyzeBoard(request);

        assertEquals(400, response.getStatusCode().value());
        verify(boardAnalyzerService, times(1)).analyzeBoardForTopCombinations(request);
    }

    @Test
    void testAnalyzeBoard_LongAnalysisTime_Success() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setHandLetters(List.of("A", "B", "C", "D", "E", "F", "G"));

        BoardAnalyzerResponse mockResponse = new BoardAnalyzerResponse();
        mockResponse.setTopCombinations(new ArrayList<>());
        mockResponse.setTotalCombinationsCount(0);
        mockResponse.setMessage("Board analysis completed with extended processing time");

        when(boardAnalyzerService.analyzeBoardForTopCombinations(request)).thenReturn(mockResponse);

        ResponseEntity<BoardAnalyzerResponse> response = boardAnalyzerController.analyzeBoard(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTotalCombinationsCount());
        verify(boardAnalyzerService, times(1)).analyzeBoardForTopCombinations(request);
    }

    @Test
    void testAnalyzeBoard_NoResults_ReturnsEmptyList() {
        BoardAnalyzerRequest request = new BoardAnalyzerRequest();
        request.setHandLetters(List.of("Z", "Z", "Z"));

        BoardAnalyzerResponse mockResponse = new BoardAnalyzerResponse();
        mockResponse.setTopCombinations(new ArrayList<>());
        mockResponse.setTotalCombinationsCount(0);
        mockResponse.setMessage("No valid word placements found");

        when(boardAnalyzerService.analyzeBoardForTopCombinations(request)).thenReturn(mockResponse);

        ResponseEntity<BoardAnalyzerResponse> response = boardAnalyzerController.analyzeBoard(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTopCombinations().size());
        assertEquals("No valid word placements found", response.getBody().getMessage());
        verify(boardAnalyzerService, times(1)).analyzeBoardForTopCombinations(request);
    }
}