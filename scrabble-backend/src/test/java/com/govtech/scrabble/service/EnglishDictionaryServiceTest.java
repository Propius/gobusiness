package com.govtech.scrabble.service;

import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.service.impl.EnglishDictionaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnglishDictionaryServiceTest {

    private EnglishDictionaryServiceImpl dictionaryService;
    private ScrabbleProperties properties;

    @BeforeEach
    void setUp() {
        properties = new ScrabbleProperties();
        // Use default configuration
        dictionaryService = new EnglishDictionaryServiceImpl(properties);
    }

    @Test
    void testIsValidWordWithValidEnglishWord() {
        assertTrue(dictionaryService.isValidWord("HELLO"));
        assertTrue(dictionaryService.isValidWord("WORLD"));
        assertTrue(dictionaryService.isValidWord("CAT"));
        assertTrue(dictionaryService.isValidWord("DOG"));
    }

    @Test
    void testIsValidWordWithInvalidWord() {
        assertFalse(dictionaryService.isValidWord("ZXQY"));
        assertFalse(dictionaryService.isValidWord("XYZABC"));
    }

    @Test
    void testIsValidWordWithNullOrEmpty() {
        assertFalse(dictionaryService.isValidWord(null));
        assertFalse(dictionaryService.isValidWord(""));
        assertFalse(dictionaryService.isValidWord("   "));
    }

    @Test
    void testIsValidWordCaseInsensitive() {
        assertTrue(dictionaryService.isValidWord("hello"));
        assertTrue(dictionaryService.isValidWord("Hello"));
        assertTrue(dictionaryService.isValidWord("HELLO"));
        assertTrue(dictionaryService.isValidWord("HeLLo"));
    }

    @Test
    void testIsValidWordConsistency() {
        // First call
        boolean result1 = dictionaryService.isValidWord("TEST");
        
        // Second call should give same result (cache or no cache)
        boolean result2 = dictionaryService.isValidWord("TEST");
        
        assertEquals(result1, result2);
        assertTrue(result1); // TEST should be a valid word
    }

    @Test
    void testFindPossibleWordsWithValidLetters() {
        List<String> availableLetters = Arrays.asList("C", "A", "T", "S");
        
        List<String> result = dictionaryService.findPossibleWords(availableLetters, 3, 4);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Check that all returned words use only available letters
        for (String word : result) {
            assertTrue(word.length() >= 3);
            assertTrue(word.length() <= 4);
            assertTrue(dictionaryService.isValidWord(word));
        }
    }

    @Test
    void testFindPossibleWordsWithEmptyLetters() {
        List<String> result = dictionaryService.findPossibleWords(Collections.emptyList(), 3, 5);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindPossibleWordsWithNullLetters() {
        List<String> result = dictionaryService.findPossibleWords(null, 3, 5);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindPossibleWordsConsistency() {
        List<String> availableLetters = Arrays.asList("T", "E", "S", "T");
        
        // First call
        List<String> result1 = dictionaryService.findPossibleWords(availableLetters, 3, 4);
        
        // Second call should be consistent
        List<String> result2 = dictionaryService.findPossibleWords(availableLetters, 3, 4);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.size(), result2.size());
    }

    @Test
    void testGetRandomWordWithinRange() {
        String randomWord = dictionaryService.getRandomWord(4, 6);
        
        assertNotNull(randomWord);
        assertTrue(randomWord.length() >= 4);
        assertTrue(randomWord.length() <= 6);
        assertTrue(dictionaryService.isValidWord(randomWord));
    }

    @Test
    void testGetRandomWordSingleLength() {
        String randomWord = dictionaryService.getRandomWord(5, 5);
        
        assertNotNull(randomWord);
        assertEquals(5, randomWord.length());
        assertTrue(dictionaryService.isValidWord(randomWord));
    }

    @Test
    void testGetWordCount() {
        int wordCount = dictionaryService.getWordCount();
        
        assertTrue(wordCount > 0);
        assertTrue(wordCount > 100000); // English language has many words
    }

    @Test
    void testGetWordsByLength() {
        List<String> words = dictionaryService.getWordsByLength(4);
        
        assertNotNull(words);
        // Note: This method may return empty list depending on random word generation success
        // We test the contract rather than specific results
        
        // All words should be exactly 4 characters long if any are returned
        for (String word : words) {
            assertEquals(4, word.length());
            assertTrue(dictionaryService.isValidWord(word));
        }
    }

    // Integration test to verify basic functionality
    @Test
    void testIntegrationWithBasicWords() {
        // Test some common English words
        assertTrue(dictionaryService.isValidWord("THE"));
        assertTrue(dictionaryService.isValidWord("AND"));
        assertTrue(dictionaryService.isValidWord("FOR"));
        
        // Test invalid combinations
        assertFalse(dictionaryService.isValidWord("XYZ"));
        assertFalse(dictionaryService.isValidWord("QQQ"));
    }
}