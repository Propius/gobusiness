package com.govtech.scrabble.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for DictionaryCache entity.
 * Tests business logic, access tracking, and data integrity.
 *
 * Coverage Target: 80%+
 * Priority: P1
 */
class DictionaryCacheTest {

    private DictionaryCache dictionaryCache;

    @BeforeEach
    void setUp() {
        dictionaryCache = new DictionaryCache();
    }

    // ========================================
    // CONSTRUCTOR TESTS
    // ========================================

    @Test
    void testDefaultConstructor_InitializesDefaults() {
        // Test that default constructor sets appropriate defaults
        DictionaryCache cache = new DictionaryCache();

        assertNotNull(cache.getCachedAt(), "CachedAt should be initialized");
        assertNotNull(cache.getLastAccessed(), "LastAccessed should be initialized");
        assertEquals(1, cache.getAccessCount(), "Access count should be initialized to 1");
        assertEquals("LANGUAGETOOL", cache.getDictionarySource(), "Default dictionary source should be LANGUAGETOOL");
    }

    @Test
    void testConstructorWithWordAndValidity() {
        // Test constructor with word and validity
        DictionaryCache cache = new DictionaryCache("HELLO", true);

        assertEquals("HELLO", cache.getWord());
        assertTrue(cache.getIsValid());
        assertNotNull(cache.getCachedAt());
        assertNotNull(cache.getLastAccessed());
        assertEquals(1, cache.getAccessCount());
        assertEquals("LANGUAGETOOL", cache.getDictionarySource());
    }

    @Test
    void testConstructorWithAllFields() {
        // Test constructor with all fields
        DictionaryCache cache = new DictionaryCache("WORLD", false, "CUSTOM_DICT");

        assertEquals("WORLD", cache.getWord());
        assertFalse(cache.getIsValid());
        assertEquals("CUSTOM_DICT", cache.getDictionarySource());
        assertNotNull(cache.getCachedAt());
        assertNotNull(cache.getLastAccessed());
        assertEquals(1, cache.getAccessCount());
    }

    @Test
    void testConstructor_TimestampsAreRecent() {
        // Test that timestamps are set to current time
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        DictionaryCache cache = new DictionaryCache("TEST", true);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertTrue(cache.getCachedAt().isAfter(before) && cache.getCachedAt().isBefore(after),
                  "CachedAt should be set to current time");
        assertTrue(cache.getLastAccessed().isAfter(before) && cache.getLastAccessed().isBefore(after),
                  "LastAccessed should be set to current time");
    }

    // ========================================
    // RECORD ACCESS TESTS
    // ========================================

    @Test
    void testRecordAccess_IncrementsAccessCount() {
        // Test that recordAccess increments the access count
        DictionaryCache cache = new DictionaryCache("HELLO", true);
        int initialCount = cache.getAccessCount();

        cache.recordAccess();

        assertEquals(initialCount + 1, cache.getAccessCount(),
                    "Access count should increment by 1");
    }

    @Test
    void testRecordAccess_UpdatesLastAccessedTimestamp() throws InterruptedException {
        // Test that recordAccess updates the last accessed timestamp
        DictionaryCache cache = new DictionaryCache("WORLD", true);
        LocalDateTime initialLastAccessed = cache.getLastAccessed();

        // Wait a moment to ensure timestamp difference
        Thread.sleep(10);
        cache.recordAccess();

        assertTrue(cache.getLastAccessed().isAfter(initialLastAccessed),
                  "LastAccessed should be updated to a more recent time");
    }

    @Test
    void testRecordAccess_MultipleAccesses() {
        // Test multiple access recordings
        DictionaryCache cache = new DictionaryCache("TEST", true);
        int initialCount = cache.getAccessCount();

        for (int i = 0; i < 10; i++) {
            cache.recordAccess();
        }

        assertEquals(initialCount + 10, cache.getAccessCount(),
                    "Access count should increment by 10 after 10 accesses");
    }

    @Test
    void testRecordAccess_PreservesOtherFields() {
        // Test that recordAccess doesn't modify other fields
        DictionaryCache cache = new DictionaryCache("PRESERVE", true, "CUSTOM");
        String originalWord = cache.getWord();
        Boolean originalValidity = cache.getIsValid();
        String originalSource = cache.getDictionarySource();
        LocalDateTime originalCachedAt = cache.getCachedAt();

        cache.recordAccess();

        assertEquals(originalWord, cache.getWord(), "Word should not change");
        assertEquals(originalValidity, cache.getIsValid(), "Validity should not change");
        assertEquals(originalSource, cache.getDictionarySource(), "Dictionary source should not change");
        assertEquals(originalCachedAt, cache.getCachedAt(), "CachedAt should not change");
    }

    // ========================================
    // CACHE HIT/MISS SCENARIO TESTS
    // ========================================

    @Test
    void testCacheHitScenario_ValidWord() {
        // Simulate a cache hit for a valid word
        DictionaryCache cache = new DictionaryCache("HELLO", true);

        // Simulate multiple cache hits
        for (int i = 0; i < 5; i++) {
            cache.recordAccess();
        }

        assertTrue(cache.getIsValid(), "Word should remain valid");
        assertEquals(6, cache.getAccessCount(), "Access count should be 6 (1 initial + 5 accesses)");
    }

    @Test
    void testCacheHitScenario_InvalidWord() {
        // Simulate a cache hit for an invalid word
        DictionaryCache cache = new DictionaryCache("ZZZZZ", false);

        cache.recordAccess();

        assertFalse(cache.getIsValid(), "Word should remain invalid");
        assertEquals(2, cache.getAccessCount(), "Access count should be 2 (1 initial + 1 access)");
    }

    @Test
    void testCacheMissScenario_NewWord() {
        // Simulate a cache miss with a new word
        DictionaryCache cache = new DictionaryCache("NEWWORD", true);

        assertEquals(1, cache.getAccessCount(), "New cache entry should have access count of 1");
        assertNotNull(cache.getCachedAt(), "CachedAt should be set");
        assertNotNull(cache.getLastAccessed(), "LastAccessed should be set");
    }

    // ========================================
    // GETTERS AND SETTERS TESTS
    // ========================================

    @Test
    void testSetAndGetWord() {
        dictionaryCache.setWord("TESTING");
        assertEquals("TESTING", dictionaryCache.getWord());
    }

    @Test
    void testSetAndGetIsValid() {
        dictionaryCache.setIsValid(true);
        assertTrue(dictionaryCache.getIsValid());

        dictionaryCache.setIsValid(false);
        assertFalse(dictionaryCache.getIsValid());
    }

    @Test
    void testSetAndGetDictionarySource() {
        dictionaryCache.setDictionarySource("OXFORD");
        assertEquals("OXFORD", dictionaryCache.getDictionarySource());
    }

    @Test
    void testSetAndGetCachedAt() {
        LocalDateTime testTime = LocalDateTime.of(2025, 9, 30, 10, 0);
        dictionaryCache.setCachedAt(testTime);
        assertEquals(testTime, dictionaryCache.getCachedAt());
    }

    @Test
    void testSetAndGetAccessCount() {
        dictionaryCache.setAccessCount(42);
        assertEquals(42, dictionaryCache.getAccessCount());
    }

    @Test
    void testSetAndGetLastAccessed() {
        LocalDateTime testTime = LocalDateTime.of(2025, 9, 30, 12, 0);
        dictionaryCache.setLastAccessed(testTime);
        assertEquals(testTime, dictionaryCache.getLastAccessed());
    }

    // ========================================
    // EDGE CASE TESTS
    // ========================================

    @Test
    void testNullWord() {
        // Test that null word can be set (validation should be handled at service layer)
        DictionaryCache cache = new DictionaryCache();
        cache.setWord(null);
        assertNull(cache.getWord());
    }

    @Test
    void testEmptyWord() {
        // Test empty word
        DictionaryCache cache = new DictionaryCache("", true);
        assertEquals("", cache.getWord());
    }

    @Test
    void testLongWord() {
        // Test very long word (within 255 character limit)
        String longWord = "A".repeat(255);
        DictionaryCache cache = new DictionaryCache(longWord, true);
        assertEquals(longWord, cache.getWord());
    }

    @Test
    void testNullValidity() {
        // Test null validity
        DictionaryCache cache = new DictionaryCache();
        cache.setIsValid(null);
        assertNull(cache.getIsValid());
    }

    @Test
    void testNullDictionarySource() {
        // Test null dictionary source
        DictionaryCache cache = new DictionaryCache();
        cache.setDictionarySource(null);
        assertNull(cache.getDictionarySource());
    }

    @Test
    void testZeroAccessCount() {
        // Test setting access count to zero
        DictionaryCache cache = new DictionaryCache("TEST", true);
        cache.setAccessCount(0);
        assertEquals(0, cache.getAccessCount());
    }

    @Test
    void testLargeAccessCount() {
        // Test large access count
        DictionaryCache cache = new DictionaryCache("POPULAR", true);
        cache.setAccessCount(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, cache.getAccessCount());
    }

    // ========================================
    // BUSINESS LOGIC TESTS
    // ========================================

    @Test
    void testWordCasePreservation() {
        // Test that word case is preserved
        DictionaryCache cache = new DictionaryCache("MiXeD", true);
        assertEquals("MiXeD", cache.getWord(), "Word case should be preserved");
    }

    @Test
    void testValidityConsistency() {
        // Test that validity can be changed (for cache invalidation scenarios)
        DictionaryCache cache = new DictionaryCache("WORD", true);
        assertTrue(cache.getIsValid());

        cache.setIsValid(false);
        assertFalse(cache.getIsValid());
    }

    @Test
    void testDictionarySourceChange() {
        // Test changing dictionary source (e.g., fallback dictionary)
        DictionaryCache cache = new DictionaryCache("TEST", true, "PRIMARY");
        assertEquals("PRIMARY", cache.getDictionarySource());

        cache.setDictionarySource("FALLBACK");
        assertEquals("FALLBACK", cache.getDictionarySource());
    }

    @Test
    void testAccessPatternTracking() {
        // Test access pattern tracking over time
        DictionaryCache cache = new DictionaryCache("FREQUENT", true);
        LocalDateTime firstAccess = cache.getLastAccessed();
        int firstCount = cache.getAccessCount();

        // Simulate multiple accesses
        cache.recordAccess();
        cache.recordAccess();
        cache.recordAccess();

        assertEquals(firstCount + 3, cache.getAccessCount());
        assertTrue(cache.getLastAccessed().isAfter(firstAccess) ||
                  cache.getLastAccessed().isEqual(firstAccess));
    }

    // ========================================
    // TOSTRING TEST
    // ========================================

    @Test
    void testToString_ContainsAllFields() {
        DictionaryCache cache = new DictionaryCache("HELLO", true, "LANGUAGETOOL");
        cache.setAccessCount(5);

        String toString = cache.toString();

        assertTrue(toString.contains("word='HELLO'"), "toString should contain word");
        assertTrue(toString.contains("isValid=true"), "toString should contain validity");
        assertTrue(toString.contains("dictionarySource='LANGUAGETOOL'"), "toString should contain source");
        assertTrue(toString.contains("accessCount=5"), "toString should contain access count");
        assertTrue(toString.contains("cachedAt="), "toString should contain cachedAt");
        assertTrue(toString.contains("lastAccessed="), "toString should contain lastAccessed");
    }

    @Test
    void testToString_NullFields() {
        DictionaryCache cache = new DictionaryCache();
        cache.setWord(null);
        cache.setIsValid(null);
        cache.setDictionarySource(null);

        String toString = cache.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("word=null") || toString.contains("word='null'"));
    }

    // ========================================
    // INTEGRATION SCENARIO TESTS
    // ========================================

    @Test
    void testCacheLifecycle_Creation() {
        // Test cache entry creation
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        DictionaryCache cache = new DictionaryCache("LIFECYCLE", true, "DICT");
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(cache.getWord());
        assertNotNull(cache.getIsValid());
        assertNotNull(cache.getDictionarySource());
        assertTrue(cache.getCachedAt().isAfter(before) && cache.getCachedAt().isBefore(after));
        assertEquals(1, cache.getAccessCount());
    }

    @Test
    void testCacheLifecycle_Usage() {
        // Test cache entry usage over time
        DictionaryCache cache = new DictionaryCache("USAGE", true);
        LocalDateTime createdAt = cache.getCachedAt();

        // Simulate usage
        for (int i = 0; i < 100; i++) {
            cache.recordAccess();
        }

        assertEquals(101, cache.getAccessCount(), "Access count should reflect all accesses");
        assertEquals(createdAt, cache.getCachedAt(), "CachedAt should remain unchanged");
        assertTrue(cache.getLastAccessed().isAfter(createdAt) ||
                  cache.getLastAccessed().isEqual(createdAt),
                  "LastAccessed should be updated");
    }

    @Test
    void testMultipleCacheEntries_Independence() {
        // Test that multiple cache entries are independent
        DictionaryCache cache1 = new DictionaryCache("WORD1", true);
        DictionaryCache cache2 = new DictionaryCache("WORD2", false);

        cache1.recordAccess();
        cache1.recordAccess();

        assertEquals(3, cache1.getAccessCount());
        assertEquals(1, cache2.getAccessCount());
        assertNotEquals(cache1.getWord(), cache2.getWord());
        assertNotEquals(cache1.getIsValid(), cache2.getIsValid());
    }

    @Test
    void testCacheEntry_PerformanceCharacteristics() {
        // Test that cache entry operations are fast
        DictionaryCache cache = new DictionaryCache("PERFORMANCE", true);

        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            cache.recordAccess();
        }
        long endTime = System.nanoTime();

        long durationMs = (endTime - startTime) / 1_000_000;
        assertTrue(durationMs < 100, "1000 recordAccess calls should complete in under 100ms");
        assertEquals(1001, cache.getAccessCount());
    }
}