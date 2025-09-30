package com.govtech.scrabble.service;

import com.govtech.scrabble.dto.CalculateScoreResponse;
import com.govtech.scrabble.dto.ScoreResponse;
import com.govtech.scrabble.entity.Score;
import com.govtech.scrabble.repository.ScoreRepository;
import com.govtech.scrabble.service.impl.ScrabbleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ScrabbleServiceTest {

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private EnglishDictionaryService englishDictionaryService;

    @InjectMocks
    private ScrabbleServiceImpl scrabbleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(englishDictionaryService.isValidWord(anyString())).thenReturn(true);
    }

    @Test
    void testCalculateScore_ExcitingWord() {
        CalculateScoreResponse response = scrabbleService.calculateScore("EXCITING");
        
        assertEquals("EXCITING", response.getWord());
        assertEquals(18, response.getTotalScore());
        assertTrue(response.getIsValidWord());
        assertNull(response.getValidationMessage());
    }

    @Test
    void testCalculateScore_EmptyWord() {
        CalculateScoreResponse response = scrabbleService.calculateScore("");
        
        assertEquals("", response.getWord());
        assertEquals(0, response.getTotalScore());
    }

    @Test
    void testCalculateScore_NullWord() {
        CalculateScoreResponse response = scrabbleService.calculateScore(null);
        
        assertEquals("", response.getWord());
        assertEquals(0, response.getTotalScore());
    }

    @Test
    void testCalculateScore_LowercaseWord() {
        CalculateScoreResponse response = scrabbleService.calculateScore("hello");
        
        assertEquals("HELLO", response.getWord());
        assertEquals(8, response.getTotalScore()); // H(4) + E(1) + L(1) + L(1) + O(1) = 8
    }

    @Test
    void testCalculateScore_HighValueLetters() {
        CalculateScoreResponse response = scrabbleService.calculateScore("QUIZ");
        
        assertEquals("QUIZ", response.getWord());
        assertEquals(22, response.getTotalScore()); // Q(10) + U(1) + I(1) + Z(10) = 22
    }

    @Test
    void testSaveScore_ValidWord() {
        Score savedScore = new Score("TEST", 4);
        savedScore.setId(1L);
        savedScore.setCreatedAt(LocalDateTime.now());

        when(scoreRepository.save(any(Score.class))).thenReturn(savedScore);

        ScoreResponse response = scrabbleService.saveScore("test");

        assertEquals("TEST", response.getWord());
        assertEquals(4, response.getPoints());
        assertEquals(1L, response.getId());
        verify(scoreRepository, times(1)).save(any(Score.class));
    }

    @Test
    void testSaveScore_EmptyWord_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> scrabbleService.saveScore("")
        );
        
        assertEquals("Cannot save score for empty or invalid word", exception.getMessage());
        verify(scoreRepository, never()).save(any(Score.class));
    }

    @Test
    void testGetTopScores() {
        Score score1 = new Score("QUIZ", 22);
        score1.setId(1L);
        Score score2 = new Score("TEST", 4);
        score2.setId(2L);

        List<Score> mockScores = Arrays.asList(score1, score2);
        when(scoreRepository.findTopScores(eq(PageRequest.of(0, 10)))).thenReturn(mockScores);

        List<ScoreResponse> responses = scrabbleService.getTopScores();

        assertEquals(2, responses.size());
        assertEquals("QUIZ", responses.get(0).getWord());
        assertEquals(22, responses.get(0).getPoints());
        assertEquals("TEST", responses.get(1).getWord());
        assertEquals(4, responses.get(1).getPoints());
    }

    @Test
    void testCalculateScore_WordWithNonLetters() {
        CalculateScoreResponse response = scrabbleService.calculateScore("HE11O");
        
        assertEquals("HE11O", response.getWord());
        assertEquals(6, response.getTotalScore()); // Only H(4) + E(1) + O(1) = 6, non-letters are ignored
        assertTrue(response.getIsValidWord());
    }

    @Test
    void testCalculateScore_InvalidWord() {
        when(englishDictionaryService.isValidWord("INVALIDWORD")).thenReturn(false);
        
        CalculateScoreResponse response = scrabbleService.calculateScore("INVALIDWORD");
        
        assertEquals("INVALIDWORD", response.getWord());
        assertEquals(19, response.getTotalScore()); // I(1) + N(1) + V(4) + A(1) + L(1) + I(1) + D(2) + W(4) + O(1) + R(1) + D(2) = 19
        assertFalse(response.getIsValidWord());
        assertEquals("Word not found in dictionary", response.getValidationMessage());
        verify(englishDictionaryService).isValidWord("INVALIDWORD");
    }

    @Test
    void testSaveScore_InvalidWord_ThrowsException() {
        when(englishDictionaryService.isValidWord("INVALIDWORD")).thenReturn(false);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> scrabbleService.saveScore("INVALIDWORD")
        );
        
        assertTrue(exception.getMessage().contains("Cannot save score for invalid word"));
        assertTrue(exception.getMessage().contains("Word not found in dictionary"));
        verify(scoreRepository, never()).save(any(Score.class));
    }

    @Test
    void testSaveScore_ValidWord_Success() {
        Score savedScore = new Score("TEST", 4);
        savedScore.setId(1L);
        savedScore.setCreatedAt(LocalDateTime.now());

        when(englishDictionaryService.isValidWord("TEST")).thenReturn(true);
        when(scoreRepository.save(any(Score.class))).thenReturn(savedScore);

        ScoreResponse response = scrabbleService.saveScore("test");

        assertEquals("TEST", response.getWord());
        assertEquals(4, response.getPoints());
        assertEquals(1L, response.getId());
        verify(englishDictionaryService).isValidWord("TEST");
        verify(scoreRepository).save(any(Score.class));
    }
}