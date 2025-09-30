package com.govtech.scrabble.service;

import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.dto.CalculateScoreResponse;
import com.govtech.scrabble.dto.ScrambleCheckResponse;
import com.govtech.scrabble.dto.ScrambleResponse;
import com.govtech.scrabble.service.impl.WordScrambleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WordScrambleServiceTest {

    @Mock
    private ScrabbleProperties scrabbleProperties;
    
    @Mock
    private ScrabbleProperties.Scramble scrambleConfig;
    
    @Mock
    private ScrabbleProperties.WordLength wordLength;
    
    @Mock
    private ScrabbleProperties.DifficultyLevel easyLevel;
    
    @Mock
    private ScrabbleProperties.DifficultyLevel mediumLevel;
    
    @Mock
    private ScrabbleProperties.DifficultyLevel hardLevel;

    @Mock
    private ScrabbleDictionary scrabbleDictionary;

    @Mock
    private EnglishDictionaryService englishDictionaryService;

    @Mock
    private ScrabbleService scrabbleService;

    @InjectMocks
    private WordScrambleServiceImpl wordScrambleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock ScrabbleDictionary as not loaded to fall back to EnglishDictionaryService
        lenient().when(scrabbleDictionary.isLoaded()).thenReturn(false);
        
        // Setup difficulty levels
        when(easyLevel.getMin()).thenReturn(4);
        when(easyLevel.getMax()).thenReturn(5);
        when(mediumLevel.getMin()).thenReturn(6);
        when(mediumLevel.getMax()).thenReturn(7);
        when(hardLevel.getMin()).thenReturn(8);
        when(hardLevel.getMax()).thenReturn(8);
        
        Map<String, ScrabbleProperties.DifficultyLevel> difficultyLevels = Map.of(
            "easy", easyLevel,
            "medium", mediumLevel,
            "hard", hardLevel
        );
        
        // Setup default mock behavior
        when(scrabbleProperties.getScramble()).thenReturn(scrambleConfig);
        when(scrambleConfig.isEnabled()).thenReturn(true);
        when(scrambleConfig.getWordLength()).thenReturn(wordLength);
        when(scrambleConfig.getDifficultyLevels()).thenReturn(difficultyLevels);
        when(wordLength.getMin()).thenReturn(4);
        when(wordLength.getMax()).thenReturn(8);
        when(englishDictionaryService.getRandomWord(anyInt(), anyInt())).thenReturn("HELLO");
    }

    @Test
    void testIsScrambleEnabled_WhenEnabled_ReturnsTrue() {
        when(scrambleConfig.isEnabled()).thenReturn(true);
        
        assertTrue(wordScrambleService.isScrambleEnabled());
    }

    @Test
    void testIsScrambleEnabled_WhenDisabled_ReturnsFalse() {
        when(scrambleConfig.isEnabled()).thenReturn(false);
        
        assertFalse(wordScrambleService.isScrambleEnabled());
    }

    @Test
    void testGenerateScramble_Success() {
        when(englishDictionaryService.getRandomWord(4, 8)).thenReturn("HELLO");
        
        ScrambleResponse response = wordScrambleService.generateScramble();
        
        assertNotNull(response);
        assertNotNull(response.getScrambledLetters());
        assertNotEquals("HELLO", response.getScrambledLetters()); // Should be scrambled
        assertEquals(5, response.getWordLength());
        assertNotNull(response.getOriginalWord()); // Session ID
        assertNotNull(response.getAvailableLetters());
        assertEquals(5, response.getAvailableLetters().size());
        assertNotNull(response.getHint());
        assertNotNull(response.getDifficulty());
    }

    @Test
    void testGenerateScramble_WhenDisabled_ThrowsException() {
        when(scrambleConfig.isEnabled()).thenReturn(false);
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> wordScrambleService.generateScramble()
        );
        
        assertEquals("Word scramble feature is disabled", exception.getMessage());
    }

    @Test
    void testGenerateScramble_EasyDifficulty() {
        when(englishDictionaryService.getRandomWord(4, 5)).thenReturn("WORD");
        
        ScrambleResponse response = wordScrambleService.generateScramble("easy");
        
        assertNotNull(response);
        assertEquals(4, response.getWordLength());
        assertEquals("easy", response.getDifficulty());
    }

    @Test
    void testGenerateScramble_MediumDifficulty() {
        when(englishDictionaryService.getRandomWord(6, 7)).thenReturn("MEDIUM");
        
        ScrambleResponse response = wordScrambleService.generateScramble("medium");
        
        assertNotNull(response);
        assertEquals(6, response.getWordLength());
        assertEquals("medium", response.getDifficulty());
    }

    @Test
    void testGenerateScramble_HardDifficulty() {
        when(englishDictionaryService.getRandomWord(8, 8)).thenReturn("COMPUTER");
        
        ScrambleResponse response = wordScrambleService.generateScramble("hard");
        
        assertNotNull(response);
        assertEquals(8, response.getWordLength());
        assertEquals("hard", response.getDifficulty());
    }

    @Test
    void testReshuffleScramble_ValidSession() {
        // First generate a scramble
        when(englishDictionaryService.getRandomWord(4, 8)).thenReturn("HELLO");
        ScrambleResponse originalScramble = wordScrambleService.generateScramble();
        String sessionId = originalScramble.getOriginalWord();
        
        // Then reshuffle
        ScrambleResponse reshuffled = wordScrambleService.reshuffleScramble(sessionId);
        
        assertNotNull(reshuffled);
        assertEquals(5, reshuffled.getWordLength());
        assertEquals(sessionId, reshuffled.getOriginalWord());
        assertNotNull(reshuffled.getScrambledLetters());
    }

    @Test
    void testReshuffleScramble_InvalidSession_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> wordScrambleService.reshuffleScramble("invalid-session")
        );
        
        assertEquals("Invalid or expired scramble session", exception.getMessage());
    }

    @Test
    void testReshuffleScramble_WhenDisabled_ThrowsException() {
        when(scrambleConfig.isEnabled()).thenReturn(false);
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> wordScrambleService.reshuffleScramble("any-session")
        );
        
        assertEquals("Word scramble feature is disabled", exception.getMessage());
    }

    @Test
    void testCheckAnswer_CorrectAnswer() {
        // Generate scramble first
        when(englishDictionaryService.getRandomWord(4, 8)).thenReturn("HELLO");
        when(scrabbleService.calculateScore("HELLO")).thenReturn(new CalculateScoreResponse("HELLO", 8));
        
        ScrambleResponse scramble = wordScrambleService.generateScramble();
        String sessionId = scramble.getOriginalWord();
        
        // Check correct answer
        ScrambleCheckResponse response = wordScrambleService.checkAnswer(sessionId, "HELLO");
        
        assertNotNull(response);
        assertTrue(response.getIsCorrect());
        assertEquals("HELLO", response.getUserAnswer());
        assertEquals("HELLO", response.getCorrectAnswer());
        assertEquals(8, response.getScore());
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("Congratulations"));
    }

    @Test
    void testCheckAnswer_IncorrectAnswer() {
        // Generate scramble first
        when(englishDictionaryService.getRandomWord(4, 8)).thenReturn("HELLO");
        ScrambleResponse scramble = wordScrambleService.generateScramble();
        String sessionId = scramble.getOriginalWord();
        
        // Check incorrect answer
        ScrambleCheckResponse response = wordScrambleService.checkAnswer(sessionId, "WORLD");
        
        assertNotNull(response);
        assertFalse(response.getIsCorrect());
        assertEquals("WORLD", response.getUserAnswer());
        assertEquals("HELLO", response.getCorrectAnswer());
        assertEquals(0, response.getScore());
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("Incorrect"));
    }

    @Test
    void testCheckAnswer_InvalidSession() {
        ScrambleCheckResponse response = wordScrambleService.checkAnswer("invalid-session", "HELLO");
        
        assertNotNull(response);
        assertFalse(response.getIsCorrect());
        assertEquals("HELLO", response.getUserAnswer());
        assertNull(response.getCorrectAnswer());
        assertEquals(0, response.getScore());
        assertTrue(response.getMessage().contains("Invalid or expired"));
    }

    @Test
    void testCheckAnswer_WhenDisabled_ThrowsException() {
        when(scrambleConfig.isEnabled()).thenReturn(false);
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> wordScrambleService.checkAnswer("any-session", "HELLO")
        );
        
        assertEquals("Word scramble feature is disabled", exception.getMessage());
    }

    @Test
    void testClearSession() {
        // Generate scramble first
        when(englishDictionaryService.getRandomWord(4, 8)).thenReturn("HELLO");
        ScrambleResponse scramble = wordScrambleService.generateScramble();
        String sessionId = scramble.getOriginalWord();
        
        // Verify session exists (can reshuffle)
        assertNotNull(wordScrambleService.reshuffleScramble(sessionId));
        
        // Clear session
        wordScrambleService.clearSession(sessionId);
        
        // Verify session is cleared (can't reshuffle)
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> wordScrambleService.reshuffleScramble(sessionId)
        );
        
        assertEquals("Invalid or expired scramble session", exception.getMessage());
    }

    @Test
    void testGetActiveSessionsCount() {
        int initialCount = wordScrambleService.getActiveSessionsCount();
        
        // Generate a scramble
        when(englishDictionaryService.getRandomWord(4, 8)).thenReturn("HELLO");
        wordScrambleService.generateScramble();
        
        assertEquals(initialCount + 1, wordScrambleService.getActiveSessionsCount());
    }

    @Test
    void testCheckAnswer_CaseInsensitive() {
        // Generate scramble first
        when(englishDictionaryService.getRandomWord(4, 8)).thenReturn("HELLO");
        when(scrabbleService.calculateScore("HELLO")).thenReturn(new CalculateScoreResponse("HELLO", 8));
        
        ScrambleResponse scramble = wordScrambleService.generateScramble();
        String sessionId = scramble.getOriginalWord();
        
        // Check with lowercase answer
        ScrambleCheckResponse response = wordScrambleService.checkAnswer(sessionId, "hello");
        
        assertNotNull(response);
        assertTrue(response.getIsCorrect());
        assertEquals("HELLO", response.getUserAnswer()); // Should be normalized to uppercase
    }

    @Test
    void testCheckAnswer_NullAnswer() {
        // Generate scramble first
        when(englishDictionaryService.getRandomWord(4, 8)).thenReturn("HELLO");
        ScrambleResponse scramble = wordScrambleService.generateScramble();
        String sessionId = scramble.getOriginalWord();
        
        // Check with null answer
        ScrambleCheckResponse response = wordScrambleService.checkAnswer(sessionId, null);
        
        assertNotNull(response);
        assertFalse(response.getIsCorrect());
        assertEquals("", response.getUserAnswer());
    }
}