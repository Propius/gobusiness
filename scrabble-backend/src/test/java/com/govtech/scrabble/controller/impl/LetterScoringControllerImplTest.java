package com.govtech.scrabble.controller.impl;

import com.govtech.scrabble.dto.LetterScoringResponse;
import com.govtech.scrabble.service.LetterScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LetterScoringControllerImplTest {

    @Mock
    private LetterScoringService letterScoringService;

    @InjectMocks
    private LetterScoringControllerImpl letterScoringController;

    @Test
    void testGetLetterScores_FeatureEnabled_Success() {
        Map<String, Integer> letterScores = new HashMap<>();
        letterScores.put("A", 1);
        letterScores.put("B", 3);
        letterScores.put("C", 3);
        letterScores.put("Q", 10);
        letterScores.put("Z", 10);

        LetterScoringResponse mockResponse = new LetterScoringResponse();
        mockResponse.setLetterScores(letterScores);
        mockResponse.setMessage("Letter scoring legend retrieved successfully");

        when(letterScoringService.isLetterScoringEnabled()).thenReturn(true);
        when(letterScoringService.getLetterScores()).thenReturn(mockResponse);

        ResponseEntity<LetterScoringResponse> response = letterScoringController.getLetterScores();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEnabled());
        assertEquals(5, response.getBody().getLetterScores().size());
        assertEquals(1, response.getBody().getLetterScores().get("A"));
        assertEquals(10, response.getBody().getLetterScores().get("Q"));
        assertEquals("Letter scoring legend retrieved successfully", response.getBody().getMessage());
        verify(letterScoringService, times(1)).isLetterScoringEnabled();
        verify(letterScoringService, times(1)).getLetterScores();
    }

    @Test
    void testGetLetterScores_FeatureDisabled_Returns200WithDisabledFlag() {
        when(letterScoringService.isLetterScoringEnabled()).thenReturn(false);

        ResponseEntity<LetterScoringResponse> response = letterScoringController.getLetterScores();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEnabled());
        assertEquals("Letter scoring display is currently disabled", response.getBody().getMessage());
        assertTrue(response.getBody().getLetterScores().isEmpty());
        verify(letterScoringService, times(1)).isLetterScoringEnabled();
        verify(letterScoringService, never()).getLetterScores();
    }

    @Test
    void testGetLetterScores_ServiceThrowsException_Returns500() {
        when(letterScoringService.isLetterScoringEnabled()).thenReturn(true);
        when(letterScoringService.getLetterScores()).thenThrow(new RuntimeException("Database connection failed"));

        ResponseEntity<LetterScoringResponse> response = letterScoringController.getLetterScores();

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Failed to retrieve letter scores"));
        assertTrue(response.getBody().getMessage().contains("Database connection failed"));
        verify(letterScoringService, times(1)).isLetterScoringEnabled();
        verify(letterScoringService, times(1)).getLetterScores();
    }

    @Test
    void testGetLetterScores_EmptyLetterScores_Success() {
        Map<String, Integer> emptyScores = new HashMap<>();

        LetterScoringResponse mockResponse = new LetterScoringResponse();
        mockResponse.setLetterScores(emptyScores);
        mockResponse.setMessage("No letter scores available");

        when(letterScoringService.isLetterScoringEnabled()).thenReturn(true);
        when(letterScoringService.getLetterScores()).thenReturn(mockResponse);

        ResponseEntity<LetterScoringResponse> response = letterScoringController.getLetterScores();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getLetterScores().size());
        assertEquals("No letter scores available", response.getBody().getMessage());
        verify(letterScoringService, times(1)).isLetterScoringEnabled();
        verify(letterScoringService, times(1)).getLetterScores();
    }

    @Test
    void testGetLetterScores_AllLetters_CorrectScores() {
        Map<String, Integer> allLetters = new HashMap<>();
        allLetters.put("A", 1);
        allLetters.put("E", 1);
        allLetters.put("I", 1);
        allLetters.put("O", 1);
        allLetters.put("U", 1);
        allLetters.put("L", 1);
        allLetters.put("N", 1);
        allLetters.put("S", 1);
        allLetters.put("T", 1);
        allLetters.put("R", 1);
        allLetters.put("D", 2);
        allLetters.put("G", 2);
        allLetters.put("B", 3);
        allLetters.put("C", 3);
        allLetters.put("M", 3);
        allLetters.put("P", 3);
        allLetters.put("F", 4);
        allLetters.put("H", 4);
        allLetters.put("V", 4);
        allLetters.put("W", 4);
        allLetters.put("Y", 4);
        allLetters.put("K", 5);
        allLetters.put("J", 8);
        allLetters.put("X", 8);
        allLetters.put("Q", 10);
        allLetters.put("Z", 10);

        LetterScoringResponse mockResponse = new LetterScoringResponse();
        mockResponse.setLetterScores(allLetters);
        mockResponse.setMessage("All letter scores retrieved");

        when(letterScoringService.isLetterScoringEnabled()).thenReturn(true);
        when(letterScoringService.getLetterScores()).thenReturn(mockResponse);

        ResponseEntity<LetterScoringResponse> response = letterScoringController.getLetterScores();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(26, response.getBody().getLetterScores().size());
        verify(letterScoringService, times(1)).isLetterScoringEnabled();
        verify(letterScoringService, times(1)).getLetterScores();
    }

    @Test
    void testGetLetterScores_ServiceReturnsNull_Returns500() {
        when(letterScoringService.isLetterScoringEnabled()).thenReturn(true);
        when(letterScoringService.getLetterScores()).thenReturn(null);

        ResponseEntity<LetterScoringResponse> response = letterScoringController.getLetterScores();

        // Controller catches exceptions and returns 500
        assertEquals(500, response.getStatusCode().value());
        verify(letterScoringService, times(1)).isLetterScoringEnabled();
        verify(letterScoringService, times(1)).getLetterScores();
    }

    @Test
    void testGetLetterScores_ChecksFeatureStateFirst() {
        when(letterScoringService.isLetterScoringEnabled()).thenReturn(false);

        letterScoringController.getLetterScores();

        verify(letterScoringService, times(1)).isLetterScoringEnabled();
        verify(letterScoringService, never()).getLetterScores();
    }
}