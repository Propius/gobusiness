package com.govtech.scrabble.service;

import com.govtech.scrabble.config.ScrabbleDictionaryConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for ScrabbleDictionary service.
 * Tests dictionary loading, validation, performance, and edge cases.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "scrabble.custom-dictionary.enabled=true",
    "scrabble.custom-dictionary.min-length=4",
    "scrabble.custom-dictionary.max-length=10"
})
class ScrabbleDictionaryTest {

    @Autowired
    private ScrabbleDictionary dictionary;

    @Autowired
    private ScrabbleDictionaryConfig config;

    @Test
    void testDictionaryLoadsSuccessfully() {
        assertTrue(dictionary.isLoaded(), "Dictionary should be loaded");
        assertTrue(dictionary.getWordCount() > 0, "Dictionary should contain words");
        assertTrue(dictionary.getWordCount() >= 200, "Dictionary should contain at least 200 words");
    }

    @Test
    void testConfigurationIsCorrect() {
        assertTrue(config.isEnabled(), "Custom dictionary should be enabled");
        assertEquals(4, config.getMinLength(), "Min length should be 4");
        assertEquals(10, config.getMaxLength(), "Max length should be 10");
        assertEquals("classpath:scrabble-dictionary.yml", config.getFilePath());
    }

    @Test
    void testLoadTimePerformance() {
        long loadTime = dictionary.getLoadTimeMs();
        assertTrue(loadTime < 1000,
            String.format("Load time should be under 1 second, was %dms", loadTime));
        assertTrue(loadTime < 500,
            String.format("Load time should ideally be under 500ms, was %dms", loadTime));
    }

    @Test
    void testValidWords() {
        // 4-letter words
        assertTrue(dictionary.isValidWord("able"), "Should recognize 'able'");
        assertTrue(dictionary.isValidWord("ABLE"), "Should be case-insensitive");
        assertTrue(dictionary.isValidWord("quiz"), "Should recognize 'quiz'");
        assertTrue(dictionary.isValidWord("jazz"), "Should recognize 'jazz'");

        // 5-letter words
        assertTrue(dictionary.isValidWord("about"), "Should recognize 'about'");
        assertTrue(dictionary.isValidWord("bread"), "Should recognize 'bread'");
        assertTrue(dictionary.isValidWord("table"), "Should recognize 'table'");

        // 6-letter words
        assertTrue(dictionary.isValidWord("friend"), "Should recognize 'friend'");
        assertTrue(dictionary.isValidWord("people"), "Should recognize 'people'");

        // 7-letter words
        assertTrue(dictionary.isValidWord("ability"), "Should recognize 'ability'");
        assertTrue(dictionary.isValidWord("forward"), "Should recognize 'forward'");

        // 8-letter words
        assertTrue(dictionary.isValidWord("absolute"), "Should recognize 'absolute'");
        assertTrue(dictionary.isValidWord("question"), "Should recognize 'question'");

        // 9-letter words
        assertTrue(dictionary.isValidWord("beautiful"), "Should recognize 'beautiful'");
        assertTrue(dictionary.isValidWord("important"), "Should recognize 'important'");

        // 10-letter words
        assertTrue(dictionary.isValidWord("absolutely"), "Should recognize 'absolutely'");
        assertTrue(dictionary.isValidWord("management"), "Should recognize 'management'");
    }

    @Test
    void testInvalidWords() {
        assertFalse(dictionary.isValidWord("xyz"), "Should reject 'xyz'");
        assertFalse(dictionary.isValidWord("qqq"), "Should reject 'qqq'");
        assertFalse(dictionary.isValidWord("atlopho"), "Should reject 'atlopho'");
        assertFalse(dictionary.isValidWord("zzzz"), "Should reject 'zzzz'");
        assertFalse(dictionary.isValidWord("abcdefgh"), "Should reject random string");
    }

    @Test
    void testWordLengthFiltering() {
        // Words shorter than 4 letters should not be loaded
        assertFalse(dictionary.isValidWord("cat"), "Should reject 3-letter word");
        assertFalse(dictionary.isValidWord("at"), "Should reject 2-letter word");
        assertFalse(dictionary.isValidWord("a"), "Should reject 1-letter word");

        // Words longer than 10 letters should not be loaded
        // (assuming they're not in the dictionary)
        assertFalse(dictionary.isValidWord("programming"), "Should reject 11-letter word (if not in dict)");
        assertFalse(dictionary.isValidWord("extraordinary"), "Should reject 13-letter word");
    }

    @Test
    void testCaseInsensitivity() {
        assertTrue(dictionary.isValidWord("able"), "Should accept lowercase");
        assertTrue(dictionary.isValidWord("ABLE"), "Should accept uppercase");
        assertTrue(dictionary.isValidWord("AblE"), "Should accept mixed case");
        assertTrue(dictionary.isValidWord("aBLe"), "Should accept mixed case");
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(dictionary.isValidWord("  able  "), "Should handle leading/trailing whitespace");
        assertTrue(dictionary.isValidWord("\table\t"), "Should handle tabs");
        assertFalse(dictionary.isValidWord(""), "Should reject empty string");
        assertFalse(dictionary.isValidWord("   "), "Should reject whitespace-only string");
    }

    @Test
    void testNullHandling() {
        assertFalse(dictionary.isValidWord(null), "Should safely handle null");
    }

    @Test
    void testWordsByLength() {
        // Test getting words by length
        Set<String> fourLetterWords = dictionary.getWordsByLength(4);
        assertNotNull(fourLetterWords, "Should return non-null set");
        assertFalse(fourLetterWords.isEmpty(), "Should have 4-letter words");
        assertTrue(fourLetterWords.contains("able"), "Should contain 'able'");

        Set<String> fiveLetterWords = dictionary.getWordsByLength(5);
        assertNotNull(fiveLetterWords, "Should return non-null set");
        assertFalse(fiveLetterWords.isEmpty(), "Should have 5-letter words");

        // Test invalid length
        Set<String> invalidLengthWords = dictionary.getWordsByLength(99);
        assertNotNull(invalidLengthWords, "Should return empty set for invalid length");
        assertTrue(invalidLengthWords.isEmpty(), "Should be empty for invalid length");
    }

    @Test
    void testGetStatistics() {
        Map<String, Object> stats = dictionary.getStatistics();
        assertNotNull(stats, "Statistics should not be null");

        assertTrue((Boolean) stats.get("enabled"), "Should show enabled");
        assertTrue((Boolean) stats.get("loaded"), "Should show loaded");
        assertTrue((Integer) stats.get("totalWords") > 0, "Should show word count");
        assertEquals(4, stats.get("minLength"), "Should show min length");
        assertEquals(10, stats.get("maxLength"), "Should show max length");

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> wordsByLength = (Map<Integer, Integer>) stats.get("wordsByLength");
        assertNotNull(wordsByLength, "Should have words by length breakdown");
        assertFalse(wordsByLength.isEmpty(), "Should have length breakdown");
    }

    @Test
    void testValidationPerformance() {
        // Test O(1) lookup performance
        long startTime = System.nanoTime();

        // Perform 1000 validations
        for (int i = 0; i < 1000; i++) {
            dictionary.isValidWord("able");
            dictionary.isValidWord("friend");
            dictionary.isValidWord("beautiful");
            dictionary.isValidWord("absolutely");
        }

        long endTime = System.nanoTime();
        long totalTimeMs = (endTime - startTime) / 1_000_000;
        double avgTimeUs = (endTime - startTime) / 4000.0 / 1000.0; // 4000 lookups in microseconds

        assertTrue(totalTimeMs < 100,
            String.format("4000 lookups should complete under 100ms, took %dms", totalTimeMs));
        assertTrue(avgTimeUs < 10,
            String.format("Average lookup should be under 10 microseconds, was %.2fus", avgTimeUs));
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        // Test thread-safety with concurrent access
        Thread[] threads = new Thread[10];
        boolean[] results = new boolean[10];

        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    results[index] = dictionary.isValidWord("able")
                        && dictionary.isValidWord("friend")
                        && !dictionary.isValidWord("xyz");
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        for (boolean result : results) {
            assertTrue(result, "All threads should complete successfully");
        }
    }

    @Test
    void testCommonScrabbleWords() {
        // High-value Scrabble words
        String[] highValueWords = {
            "quiz", "jazz", "quake", "quartz", // Q words
            "jiffy", "jinx", "jungle", "jellybean", // J words
            "xerox", "xenial", "xenogamy", "xylophone", "xenolithic", // X words
            "zebra", "zipper", "zealous", "zucchini", "zinfandel", "zigzagging" // Z words
        };

        for (String word : highValueWords) {
            assertTrue(dictionary.isValidWord(word),
                String.format("Should recognize high-value Scrabble word: %s", word));
        }
    }

    @Test
    void testWordListCoverage() {
        // Ensure good coverage across all lengths
        for (int length = 4; length <= 10; length++) {
            Set<String> words = dictionary.getWordsByLength(length);
            assertFalse(words.isEmpty(),
                String.format("Should have words of length %d", length));
            assertTrue(words.size() >= 10,
                String.format("Should have at least 10 words of length %d", length));
        }
    }

    @Test
    void testMemoryEfficiency() {
        // Estimate memory usage
        int wordCount = dictionary.getWordCount();
        long estimatedMemoryKB = (wordCount * 8) / 1024; // Rough estimate

        assertTrue(estimatedMemoryKB < 10240,
            String.format("Memory usage should be under 10MB, estimated: %dKB", estimatedMemoryKB));
    }
}