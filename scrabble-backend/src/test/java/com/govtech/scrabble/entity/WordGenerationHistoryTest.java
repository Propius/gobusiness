package com.govtech.scrabble.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for WordGenerationHistory entity.
 * Tests metrics calculation, success tracking, and performance data.
 *
 * Coverage Target: 80%+
 * Priority: P1
 */
class WordGenerationHistoryTest {

    private WordGenerationHistory history;

    @BeforeEach
    void setUp() {
        history = new WordGenerationHistory();
    }

    // ========================================
    // CONSTRUCTOR TESTS
    // ========================================

    @Test
    void testDefaultConstructor_InitializesDefaults() {
        // Test that default constructor sets appropriate defaults
        WordGenerationHistory hist = new WordGenerationHistory();

        assertNotNull(hist.getGeneratedAt(), "GeneratedAt should be initialized");
        assertEquals(0, hist.getResultsCount(), "Results count should be initialized to 0");
        assertEquals(0, hist.getExecutionTimeMs(), "Execution time should be initialized to 0");
    }

    @Test
    void testConstructorWithRequiredFields() {
        // Test constructor with required fields
        WordGenerationHistory hist = new WordGenerationHistory(
            "ABCDEFG", "sampling", 25, 150
        );

        assertEquals("ABCDEFG", hist.getInputLetters());
        assertEquals("sampling", hist.getGenerationMode());
        assertEquals(25, hist.getResultsCount());
        assertEquals(150, hist.getExecutionTimeMs());
        assertNotNull(hist.getGeneratedAt());
    }

    @Test
    void testConstructorWithAllFields() {
        // Test constructor with all fields including user session
        WordGenerationHistory hist = new WordGenerationHistory(
            "TESTING", "exhaustive", 100, 500, "user-session-123"
        );

        assertEquals("TESTING", hist.getInputLetters());
        assertEquals("exhaustive", hist.getGenerationMode());
        assertEquals(100, hist.getResultsCount());
        assertEquals(500, hist.getExecutionTimeMs());
        assertEquals("user-session-123", hist.getUserSession());
        assertNotNull(hist.getGeneratedAt());
    }

    @Test
    void testConstructor_TimestampIsRecent() {
        // Test that timestamp is set to current time
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        WordGenerationHistory hist = new WordGenerationHistory("ABC", "sampling", 10, 100);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertTrue(hist.getGeneratedAt().isAfter(before) && hist.getGeneratedAt().isBefore(after),
                  "GeneratedAt should be set to current time");
    }

    // ========================================
    // AVERAGE TIME PER WORD TESTS
    // ========================================

    @Test
    void testGetAverageTimePerWord_NormalCase() {
        // Test average time calculation with normal values
        WordGenerationHistory hist = new WordGenerationHistory("LETTERS", "sampling", 10, 100);

        double avgTime = hist.getAverageTimePerWord();

        assertEquals(10.0, avgTime, 0.001, "Average time should be 100/10 = 10.0");
    }

    @Test
    void testGetAverageTimePerWord_SingleWord() {
        // Test average time with single word
        WordGenerationHistory hist = new WordGenerationHistory("A", "sampling", 1, 50);

        double avgTime = hist.getAverageTimePerWord();

        assertEquals(50.0, avgTime, 0.001, "Average time should be 50/1 = 50.0");
    }

    @Test
    void testGetAverageTimePerWord_ManyWords() {
        // Test average time with many words
        WordGenerationHistory hist = new WordGenerationHistory("ABCDEFG", "exhaustive", 500, 2500);

        double avgTime = hist.getAverageTimePerWord();

        assertEquals(5.0, avgTime, 0.001, "Average time should be 2500/500 = 5.0");
    }

    @Test
    void testGetAverageTimePerWord_ZeroResults() {
        // Test average time when no results
        WordGenerationHistory hist = new WordGenerationHistory("XYZ", "sampling", 0, 100);

        double avgTime = hist.getAverageTimePerWord();

        assertEquals(0.0, avgTime, 0.001, "Average time should be 0.0 when no results");
    }

    @Test
    void testGetAverageTimePerWord_NullResultsCount() {
        // Test average time with null results count
        WordGenerationHistory hist = new WordGenerationHistory();
        hist.setInputLetters("TEST");
        hist.setGenerationMode("sampling");
        hist.setResultsCount(null);
        hist.setExecutionTimeMs(100);

        double avgTime = hist.getAverageTimePerWord();

        assertEquals(0.0, avgTime, 0.001, "Average time should be 0.0 when results count is null");
    }

    @Test
    void testGetAverageTimePerWord_NullExecutionTime() {
        // Test average time with null execution time
        WordGenerationHistory hist = new WordGenerationHistory();
        hist.setInputLetters("TEST");
        hist.setGenerationMode("sampling");
        hist.setResultsCount(10);
        hist.setExecutionTimeMs(null);

        double avgTime = hist.getAverageTimePerWord();

        assertEquals(0.0, avgTime, 0.001, "Average time should be 0.0 when execution time is null");
    }

    @Test
    void testGetAverageTimePerWord_FractionalResult() {
        // Test average time with fractional result
        WordGenerationHistory hist = new WordGenerationHistory("ABC", "sampling", 3, 10);

        double avgTime = hist.getAverageTimePerWord();

        assertEquals(3.333, avgTime, 0.001, "Average time should be 10/3 = 3.333...");
    }

    @Test
    void testGetAverageTimePerWord_VeryLargeNumbers() {
        // Test average time with large numbers
        WordGenerationHistory hist = new WordGenerationHistory(
            "LONGTEXT", "exhaustive", 10000, 50000
        );

        double avgTime = hist.getAverageTimePerWord();

        assertEquals(5.0, avgTime, 0.001, "Average time should be 50000/10000 = 5.0");
    }

    // ========================================
    // WAS SUCCESSFUL TESTS
    // ========================================

    @Test
    void testWasSuccessful_WithResults() {
        // Test success detection with results
        WordGenerationHistory hist = new WordGenerationHistory("ABC", "sampling", 10, 100);

        assertTrue(hist.wasSuccessful(), "Should be successful when results > 0");
    }

    @Test
    void testWasSuccessful_NoResults() {
        // Test success detection with no results
        WordGenerationHistory hist = new WordGenerationHistory("XYZ", "sampling", 0, 100);

        assertFalse(hist.wasSuccessful(), "Should not be successful when results = 0");
    }

    @Test
    void testWasSuccessful_NullResults() {
        // Test success detection with null results
        WordGenerationHistory hist = new WordGenerationHistory();
        hist.setResultsCount(null);

        assertFalse(hist.wasSuccessful(), "Should not be successful when results is null");
    }

    @Test
    void testWasSuccessful_SingleResult() {
        // Test success with single result
        WordGenerationHistory hist = new WordGenerationHistory("A", "sampling", 1, 50);

        assertTrue(hist.wasSuccessful(), "Should be successful with 1 result");
    }

    @Test
    void testWasSuccessful_NegativeResults() {
        // Test success with negative results (edge case)
        WordGenerationHistory hist = new WordGenerationHistory();
        hist.setResultsCount(-1);

        assertFalse(hist.wasSuccessful(), "Should not be successful with negative results");
    }

    // ========================================
    // GETTERS AND SETTERS TESTS
    // ========================================

    @Test
    void testSetAndGetId() {
        history.setId(100L);
        assertEquals(100L, history.getId());
    }

    @Test
    void testSetAndGetInputLetters() {
        history.setInputLetters("TESTING");
        assertEquals("TESTING", history.getInputLetters());
    }

    @Test
    void testSetAndGetGenerationMode() {
        history.setGenerationMode("sampling");
        assertEquals("sampling", history.getGenerationMode());

        history.setGenerationMode("exhaustive");
        assertEquals("exhaustive", history.getGenerationMode());
    }

    @Test
    void testSetAndGetResultsCount() {
        history.setResultsCount(42);
        assertEquals(42, history.getResultsCount());
    }

    @Test
    void testSetAndGetExecutionTimeMs() {
        history.setExecutionTimeMs(1500);
        assertEquals(1500, history.getExecutionTimeMs());
    }

    @Test
    void testSetAndGetGeneratedAt() {
        LocalDateTime testTime = LocalDateTime.of(2025, 9, 30, 10, 0);
        history.setGeneratedAt(testTime);
        assertEquals(testTime, history.getGeneratedAt());
    }

    @Test
    void testSetAndGetUserSession() {
        history.setUserSession("user-session-456");
        assertEquals("user-session-456", history.getUserSession());
    }

    // ========================================
    // EDGE CASE TESTS
    // ========================================

    @Test
    void testNullInputLetters() {
        history.setInputLetters(null);
        assertNull(history.getInputLetters());
    }

    @Test
    void testEmptyInputLetters() {
        history.setInputLetters("");
        assertEquals("", history.getInputLetters());
    }

    @Test
    void testLongInputLetters() {
        // Test very long input (within 255 character limit)
        String longInput = "A".repeat(255);
        history.setInputLetters(longInput);
        assertEquals(longInput, history.getInputLetters());
    }

    @Test
    void testInvalidGenerationMode() {
        // Test invalid mode (validation should be handled at service layer)
        history.setGenerationMode("invalid");
        assertEquals("invalid", history.getGenerationMode());
    }

    @Test
    void testZeroExecutionTime() {
        // Test zero execution time (instant completion)
        WordGenerationHistory hist = new WordGenerationHistory("FAST", "sampling", 10, 0);
        assertEquals(0, hist.getExecutionTimeMs());
        assertEquals(0.0, hist.getAverageTimePerWord(), 0.001);
    }

    @Test
    void testLargeExecutionTime() {
        // Test very large execution time
        history.setExecutionTimeMs(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, history.getExecutionTimeMs());
    }

    @Test
    void testNullUserSession() {
        history.setUserSession(null);
        assertNull(history.getUserSession());
    }

    // ========================================
    // BUSINESS LOGIC TESTS
    // ========================================

    @Test
    void testSamplingMode_LowResultCount() {
        // Test sampling mode typically has lower result count
        WordGenerationHistory hist = new WordGenerationHistory(
            "ABCDEFG", "sampling", 50, 200
        );

        assertEquals("sampling", hist.getGenerationMode());
        assertTrue(hist.wasSuccessful());
        assertEquals(4.0, hist.getAverageTimePerWord(), 0.001);
    }

    @Test
    void testExhaustiveMode_HighResultCount() {
        // Test exhaustive mode typically has higher result count
        WordGenerationHistory hist = new WordGenerationHistory(
            "ABCDEFG", "exhaustive", 500, 5000
        );

        assertEquals("exhaustive", hist.getGenerationMode());
        assertTrue(hist.wasSuccessful());
        assertEquals(10.0, hist.getAverageTimePerWord(), 0.001);
    }

    @Test
    void testFailedGeneration_NoResults() {
        // Test failed generation with no results
        WordGenerationHistory hist = new WordGenerationHistory(
            "ZZQQXX", "sampling", 0, 250
        );

        assertFalse(hist.wasSuccessful());
        assertEquals(0.0, hist.getAverageTimePerWord(), 0.001);
        assertEquals(250, hist.getExecutionTimeMs(), "Execution time recorded even on failure");
    }

    @Test
    void testPerformanceMetrics_FastGeneration() {
        // Test fast generation performance
        WordGenerationHistory hist = new WordGenerationHistory(
            "AB", "sampling", 5, 25
        );

        assertEquals(5.0, hist.getAverageTimePerWord(), 0.001, "Fast: 25ms / 5 words = 5ms/word");
        assertTrue(hist.wasSuccessful());
    }

    @Test
    void testPerformanceMetrics_SlowGeneration() {
        // Test slow generation performance
        WordGenerationHistory hist = new WordGenerationHistory(
            "ABCDEFGHIJ", "exhaustive", 10, 10000
        );

        assertEquals(1000.0, hist.getAverageTimePerWord(), 0.001, "Slow: 10000ms / 10 words = 1000ms/word");
        assertTrue(hist.wasSuccessful());
    }

    @Test
    void testUsagePattern_SameLetters_DifferentModes() {
        // Test same letters with different generation modes
        WordGenerationHistory sampling = new WordGenerationHistory(
            "ABCDEFG", "sampling", 50, 100
        );
        WordGenerationHistory exhaustive = new WordGenerationHistory(
            "ABCDEFG", "exhaustive", 500, 2000
        );

        assertEquals("sampling", sampling.getGenerationMode());
        assertEquals("exhaustive", exhaustive.getGenerationMode());
        assertTrue(exhaustive.getResultsCount() > sampling.getResultsCount(),
                  "Exhaustive should find more words");
    }

    // ========================================
    // TOSTRING TEST
    // ========================================

    @Test
    void testToString_ContainsAllFields() {
        WordGenerationHistory hist = new WordGenerationHistory(
            "HELLO", "sampling", 25, 150, "session123"
        );
        hist.setId(42L);

        String toString = hist.toString();

        assertTrue(toString.contains("id=42"), "toString should contain id");
        assertTrue(toString.contains("inputLetters='HELLO'"), "toString should contain inputLetters");
        assertTrue(toString.contains("generationMode='sampling'"), "toString should contain generationMode");
        assertTrue(toString.contains("resultsCount=25"), "toString should contain resultsCount");
        assertTrue(toString.contains("executionTimeMs=150"), "toString should contain executionTimeMs");
        assertTrue(toString.contains("userSession='session123'"), "toString should contain userSession");
        assertTrue(toString.contains("generatedAt="), "toString should contain generatedAt");
    }

    @Test
    void testToString_NullFields() {
        WordGenerationHistory hist = new WordGenerationHistory();
        hist.setInputLetters(null);
        hist.setGenerationMode(null);
        hist.setUserSession(null);

        String toString = hist.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("null") || toString.contains("inputLetters="));
    }

    // ========================================
    // INTEGRATION SCENARIO TESTS
    // ========================================

    @Test
    void testScenario_SuccessfulSamplingGeneration() {
        // Scenario: Successful sampling generation
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        WordGenerationHistory hist = new WordGenerationHistory(
            "SCRABBLE", "sampling", 45, 250, "user-001"
        );
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertTrue(hist.wasSuccessful());
        assertEquals(45, hist.getResultsCount());
        assertEquals(5.555, hist.getAverageTimePerWord(), 0.01);
        assertTrue(hist.getGeneratedAt().isAfter(before) && hist.getGeneratedAt().isBefore(after));
    }

    @Test
    void testScenario_SuccessfulExhaustiveGeneration() {
        // Scenario: Successful exhaustive generation
        WordGenerationHistory hist = new WordGenerationHistory(
            "LETTERS", "exhaustive", 350, 5000, "user-002"
        );

        assertTrue(hist.wasSuccessful());
        assertEquals(350, hist.getResultsCount());
        assertEquals(14.285, hist.getAverageTimePerWord(), 0.01);
        assertEquals("exhaustive", hist.getGenerationMode());
    }

    @Test
    void testScenario_FailedGeneration_InvalidLetters() {
        // Scenario: Failed generation with invalid letters
        WordGenerationHistory hist = new WordGenerationHistory(
            "ZZQQXX", "sampling", 0, 180, "user-003"
        );

        assertFalse(hist.wasSuccessful());
        assertEquals(0, hist.getResultsCount());
        assertEquals(0.0, hist.getAverageTimePerWord(), 0.001);
        assertEquals(180, hist.getExecutionTimeMs(), "Time recorded even on failure");
    }

    @Test
    void testScenario_QuickGeneration_FewLetters() {
        // Scenario: Quick generation with few letters
        WordGenerationHistory hist = new WordGenerationHistory(
            "AB", "sampling", 3, 15
        );

        assertTrue(hist.wasSuccessful());
        assertEquals(3, hist.getResultsCount());
        assertEquals(5.0, hist.getAverageTimePerWord(), 0.001);
        assertTrue(hist.getExecutionTimeMs() < 100, "Should be fast with few letters");
    }

    @Test
    void testScenario_LongGeneration_ManyLetters() {
        // Scenario: Long generation with many letters
        WordGenerationHistory hist = new WordGenerationHistory(
            "ABCDEFGHIJKLM", "exhaustive", 1000, 30000
        );

        assertTrue(hist.wasSuccessful());
        assertEquals(1000, hist.getResultsCount());
        assertEquals(30.0, hist.getAverageTimePerWord(), 0.001);
        assertTrue(hist.getExecutionTimeMs() > 10000, "Should take longer with many letters");
    }

    @Test
    void testMultipleGenerations_Independence() {
        // Test that multiple generation records are independent
        WordGenerationHistory hist1 = new WordGenerationHistory("ABC", "sampling", 10, 100);
        WordGenerationHistory hist2 = new WordGenerationHistory("XYZ", "exhaustive", 50, 500);

        assertNotEquals(hist1.getInputLetters(), hist2.getInputLetters());
        assertNotEquals(hist1.getGenerationMode(), hist2.getGenerationMode());
        assertNotEquals(hist1.getResultsCount(), hist2.getResultsCount());
        assertNotEquals(hist1.getExecutionTimeMs(), hist2.getExecutionTimeMs());
    }

    @Test
    void testHistoryTracking_PerformanceComparison() {
        // Test performance comparison between modes
        WordGenerationHistory sampling = new WordGenerationHistory(
            "TESTING", "sampling", 30, 150
        );
        WordGenerationHistory exhaustive = new WordGenerationHistory(
            "TESTING", "exhaustive", 200, 2000
        );

        // Sampling should be faster per word but find fewer words
        assertTrue(sampling.getAverageTimePerWord() < exhaustive.getAverageTimePerWord());
        assertTrue(sampling.getResultsCount() < exhaustive.getResultsCount());
        assertTrue(sampling.getExecutionTimeMs() < exhaustive.getExecutionTimeMs());
    }

    @Test
    void testHistoryEntry_DataIntegrity() {
        // Test data integrity over the lifecycle of a history entry
        WordGenerationHistory hist = new WordGenerationHistory(
            "INTEGRITY", "sampling", 75, 450, "session-999"
        );

        // Verify all fields are set correctly
        assertEquals("INTEGRITY", hist.getInputLetters());
        assertEquals("sampling", hist.getGenerationMode());
        assertEquals(75, hist.getResultsCount());
        assertEquals(450, hist.getExecutionTimeMs());
        assertEquals("session-999", hist.getUserSession());
        assertNotNull(hist.getGeneratedAt());
        assertTrue(hist.wasSuccessful());
        assertEquals(6.0, hist.getAverageTimePerWord(), 0.001);
    }
}