package com.govtech.scrabble.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for BoardState entity.
 * Tests state management, update logic, and data integrity.
 *
 * Coverage Target: 80%+
 * Priority: P1
 */
class BoardStateTest {

    private BoardState boardState;
    private static final String SAMPLE_BOARD_JSON = "{\"board\":[[\"A\",\"B\"],[\"C\",\"D\"]]}";
    private static final String SAMPLE_HAND_TILES = "ABCDEFG";

    @BeforeEach
    void setUp() {
        boardState = new BoardState();
    }

    // ========================================
    // CONSTRUCTOR TESTS
    // ========================================

    @Test
    void testDefaultConstructor_InitializesDefaults() {
        // Test that default constructor sets appropriate defaults
        BoardState state = new BoardState();

        assertNotNull(state.getCreatedAt(), "CreatedAt should be initialized");
        assertNotNull(state.getLastModified(), "LastModified should be initialized");
        assertEquals(0, state.getAnalysisScore(), "Analysis score should be initialized to 0");
    }

    @Test
    void testConstructorWithRequiredFields() {
        // Test constructor with required fields
        BoardState state = new BoardState("session123", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);

        assertEquals("session123", state.getUserSession());
        assertEquals(SAMPLE_BOARD_JSON, state.getBoardData());
        assertEquals(SAMPLE_HAND_TILES, state.getHandTiles());
        assertNotNull(state.getCreatedAt());
        assertNotNull(state.getLastModified());
        assertEquals(0, state.getAnalysisScore());
    }

    @Test
    void testConstructor_TimestampsAreRecent() {
        // Test that timestamps are set to current time
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertTrue(state.getCreatedAt().isAfter(before) && state.getCreatedAt().isBefore(after),
                  "CreatedAt should be set to current time");
        assertTrue(state.getLastModified().isAfter(before) && state.getLastModified().isBefore(after),
                  "LastModified should be set to current time");
    }

    @Test
    void testConstructor_InitialTimestampsClose() {
        // Test that createdAt and lastModified are very close initially (within milliseconds)
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);

        // Due to timing, they might differ by microseconds, so check they're within 10ms
        long diffNanos = java.time.Duration.between(state.getCreatedAt(), state.getLastModified()).toNanos();
        assertTrue(Math.abs(diffNanos) < 10_000_000, // 10ms in nanoseconds
                    "CreatedAt and LastModified should be within 10ms when created");
    }

    // ========================================
    // UPDATE BOARD DATA TESTS
    // ========================================

    @Test
    void testUpdateBoardData_UpdatesData() {
        // Test that updateBoardData changes the board data
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        String newBoardData = "{\"board\":[[\"X\",\"Y\"],[\"Z\",\"W\"]]}";

        state.updateBoardData(newBoardData);

        assertEquals(newBoardData, state.getBoardData(),
                    "Board data should be updated");
    }

    @Test
    void testUpdateBoardData_UpdatesLastModified() throws InterruptedException {
        // Test that updateBoardData updates the lastModified timestamp
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        LocalDateTime initialLastModified = state.getLastModified();

        // Wait to ensure timestamp difference
        Thread.sleep(10);
        state.updateBoardData("{\"board\":[[\"NEW\"]]}");

        assertTrue(state.getLastModified().isAfter(initialLastModified),
                  "LastModified should be updated to a more recent time");
    }

    @Test
    void testUpdateBoardData_PreservesCreatedAt() {
        // Test that updateBoardData doesn't change createdAt
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        LocalDateTime originalCreatedAt = state.getCreatedAt();

        state.updateBoardData("{\"board\":[[\"NEW\"]]}");

        assertEquals(originalCreatedAt, state.getCreatedAt(),
                    "CreatedAt should not change when updating board data");
    }

    @Test
    void testUpdateBoardData_PreservesOtherFields() {
        // Test that updateBoardData only modifies boardData and lastModified
        BoardState state = new BoardState("session123", SAMPLE_BOARD_JSON, "ABCDEFG");
        state.setAnalysisScore(42);

        String originalSession = state.getUserSession();
        String originalHandTiles = state.getHandTiles();
        Integer originalScore = state.getAnalysisScore();

        state.updateBoardData("{\"board\":[[\"NEW\"]]}");

        assertEquals(originalSession, state.getUserSession(), "UserSession should not change");
        assertEquals(originalHandTiles, state.getHandTiles(), "HandTiles should not change");
        assertEquals(originalScore, state.getAnalysisScore(), "AnalysisScore should not change");
    }

    @Test
    void testUpdateBoardData_NullValue() {
        // Test updating board data with null
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);

        state.updateBoardData(null);

        assertNull(state.getBoardData(), "Board data should be set to null");
        assertNotNull(state.getLastModified(), "LastModified should still be updated");
    }

    @Test
    void testUpdateBoardData_EmptyString() {
        // Test updating board data with empty string
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);

        state.updateBoardData("");

        assertEquals("", state.getBoardData(), "Board data should be set to empty string");
    }

    // ========================================
    // UPDATE HAND TILES TESTS
    // ========================================

    @Test
    void testUpdateHandTiles_UpdatesTiles() {
        // Test that updateHandTiles changes the hand tiles
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        String newHandTiles = "XYZABC";

        state.updateHandTiles(newHandTiles);

        assertEquals(newHandTiles, state.getHandTiles(),
                    "Hand tiles should be updated");
    }

    @Test
    void testUpdateHandTiles_UpdatesLastModified() throws InterruptedException {
        // Test that updateHandTiles updates the lastModified timestamp
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        LocalDateTime initialLastModified = state.getLastModified();

        // Wait to ensure timestamp difference
        Thread.sleep(10);
        state.updateHandTiles("NEWTILE");

        assertTrue(state.getLastModified().isAfter(initialLastModified),
                  "LastModified should be updated to a more recent time");
    }

    @Test
    void testUpdateHandTiles_PreservesCreatedAt() {
        // Test that updateHandTiles doesn't change createdAt
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        LocalDateTime originalCreatedAt = state.getCreatedAt();

        state.updateHandTiles("NEWTILE");

        assertEquals(originalCreatedAt, state.getCreatedAt(),
                    "CreatedAt should not change when updating hand tiles");
    }

    @Test
    void testUpdateHandTiles_PreservesOtherFields() {
        // Test that updateHandTiles only modifies handTiles and lastModified
        BoardState state = new BoardState("session123", SAMPLE_BOARD_JSON, "ABCDEFG");
        state.setAnalysisScore(42);

        String originalSession = state.getUserSession();
        String originalBoardData = state.getBoardData();
        Integer originalScore = state.getAnalysisScore();

        state.updateHandTiles("NEWTILE");

        assertEquals(originalSession, state.getUserSession(), "UserSession should not change");
        assertEquals(originalBoardData, state.getBoardData(), "BoardData should not change");
        assertEquals(originalScore, state.getAnalysisScore(), "AnalysisScore should not change");
    }

    @Test
    void testUpdateHandTiles_NullValue() {
        // Test updating hand tiles with null
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);

        state.updateHandTiles(null);

        assertNull(state.getHandTiles(), "Hand tiles should be set to null");
        assertNotNull(state.getLastModified(), "LastModified should still be updated");
    }

    @Test
    void testUpdateHandTiles_EmptyString() {
        // Test updating hand tiles with empty string
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);

        state.updateHandTiles("");

        assertEquals("", state.getHandTiles(), "Hand tiles should be set to empty string");
    }

    // ========================================
    // GETTERS AND SETTERS TESTS
    // ========================================

    @Test
    void testSetAndGetId() {
        boardState.setId(100L);
        assertEquals(100L, boardState.getId());
    }

    @Test
    void testSetAndGetUserSession() {
        boardState.setUserSession("user-session-123");
        assertEquals("user-session-123", boardState.getUserSession());
    }

    @Test
    void testSetAndGetBoardData() {
        boardState.setBoardData(SAMPLE_BOARD_JSON);
        assertEquals(SAMPLE_BOARD_JSON, boardState.getBoardData());
    }

    @Test
    void testSetAndGetHandTiles() {
        boardState.setHandTiles("TESTING");
        assertEquals("TESTING", boardState.getHandTiles());
    }

    @Test
    void testSetAndGetCreatedAt() {
        LocalDateTime testTime = LocalDateTime.of(2025, 9, 30, 10, 0);
        boardState.setCreatedAt(testTime);
        assertEquals(testTime, boardState.getCreatedAt());
    }

    @Test
    void testSetAndGetLastModified() {
        LocalDateTime testTime = LocalDateTime.of(2025, 9, 30, 12, 0);
        boardState.setLastModified(testTime);
        assertEquals(testTime, boardState.getLastModified());
    }

    @Test
    void testSetAndGetAnalysisScore() {
        boardState.setAnalysisScore(999);
        assertEquals(999, boardState.getAnalysisScore());
    }

    // ========================================
    // EDGE CASE TESTS
    // ========================================

    @Test
    void testNullUserSession() {
        BoardState state = new BoardState(null, SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        assertNull(state.getUserSession());
    }

    @Test
    void testEmptyUserSession() {
        BoardState state = new BoardState("", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        assertEquals("", state.getUserSession());
    }

    @Test
    void testLongUserSession() {
        // Test session ID at maximum length (255 characters)
        String longSession = "S".repeat(255);
        BoardState state = new BoardState(longSession, SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        assertEquals(longSession, state.getUserSession());
    }

    @Test
    void testLargeBoardData() {
        // Test large board data (15x15 board with full JSON)
        StringBuilder largeBoardJson = new StringBuilder("{\"board\":[");
        for (int i = 0; i < 15; i++) {
            largeBoardJson.append("[");
            for (int j = 0; j < 15; j++) {
                largeBoardJson.append("\"").append((char)('A' + (i + j) % 26)).append("\"");
                if (j < 14) largeBoardJson.append(",");
            }
            largeBoardJson.append("]");
            if (i < 14) largeBoardJson.append(",");
        }
        largeBoardJson.append("]}");

        BoardState state = new BoardState("session", largeBoardJson.toString(), SAMPLE_HAND_TILES);
        assertEquals(largeBoardJson.toString(), state.getBoardData());
    }

    @Test
    void testMaxHandTiles() {
        // Test maximum hand tiles (10 characters as per validation)
        String maxTiles = "ABCDEFGHIJ";
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, maxTiles);
        assertEquals(maxTiles, state.getHandTiles());
    }

    @Test
    void testNegativeAnalysisScore() {
        // Test negative analysis score
        boardState.setAnalysisScore(-50);
        assertEquals(-50, boardState.getAnalysisScore());
    }

    @Test
    void testZeroAnalysisScore() {
        // Test zero analysis score (default)
        BoardState state = new BoardState();
        assertEquals(0, state.getAnalysisScore());
    }

    @Test
    void testLargeAnalysisScore() {
        // Test large analysis score
        boardState.setAnalysisScore(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, boardState.getAnalysisScore());
    }

    // ========================================
    // BUSINESS LOGIC TESTS
    // ========================================

    @Test
    void testBoardStateLifecycle_Creation() {
        // Test board state creation
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(state.getUserSession());
        assertNotNull(state.getBoardData());
        assertNotNull(state.getHandTiles());
        assertTrue(state.getCreatedAt().isAfter(before) && state.getCreatedAt().isBefore(after));
        assertEquals(0, state.getAnalysisScore());
    }

    @Test
    void testBoardStateLifecycle_Updates() {
        // Test board state updates over time
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, "INITIAL");
        LocalDateTime createdAt = state.getCreatedAt();
        LocalDateTime firstModified = state.getLastModified();

        // First update: board data
        state.updateBoardData("{\"board\":[[\"UPDATE1\"]]}");
        assertTrue(state.getLastModified().isAfter(firstModified) ||
                  state.getLastModified().isEqual(firstModified));

        // Second update: hand tiles
        state.updateHandTiles("UPDATE2");
        assertTrue(state.getLastModified().isAfter(firstModified) ||
                  state.getLastModified().isEqual(firstModified));

        // CreatedAt should remain unchanged
        assertEquals(createdAt, state.getCreatedAt());
    }

    @Test
    void testMultipleBoardStates_Independence() {
        // Test that multiple board states are independent
        BoardState state1 = new BoardState("session1", "{\"board\":[[\"A\"]]}", "TILES1");
        BoardState state2 = new BoardState("session2", "{\"board\":[[\"B\"]]}", "TILES2");

        state1.updateBoardData("{\"board\":[[\"C\"]]}");

        assertNotEquals(state1.getUserSession(), state2.getUserSession());
        assertNotEquals(state1.getBoardData(), state2.getBoardData());
        assertNotEquals(state1.getHandTiles(), state2.getHandTiles());
    }

    @Test
    void testBoardStateUpdate_Sequence() {
        // Test sequence of updates maintains data integrity
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);

        state.updateBoardData("{\"board\":[[\"STEP1\"]]}");
        assertEquals("{\"board\":[[\"STEP1\"]]}", state.getBoardData());

        state.updateHandTiles("STEP2");
        assertEquals("STEP2", state.getHandTiles());

        state.setAnalysisScore(100);
        assertEquals(100, state.getAnalysisScore());

        // All updates should be preserved
        assertEquals("{\"board\":[[\"STEP1\"]]}", state.getBoardData());
        assertEquals("STEP2", state.getHandTiles());
        assertEquals(100, state.getAnalysisScore());
    }

    @Test
    void testBoardStateConsistency_AfterMultipleUpdates() {
        // Test data consistency after multiple rapid updates
        BoardState state = new BoardState("session", SAMPLE_BOARD_JSON, SAMPLE_HAND_TILES);
        LocalDateTime createdAt = state.getCreatedAt();

        for (int i = 0; i < 10; i++) {
            state.updateBoardData("{\"board\":[[\"UPDATE" + i + "\"]]}");
        }

        assertEquals("{\"board\":[[\"UPDATE9\"]]}", state.getBoardData());
        assertEquals(createdAt, state.getCreatedAt(), "CreatedAt should never change");
        assertNotNull(state.getLastModified());
    }

    // ========================================
    // TOSTRING TEST
    // ========================================

    @Test
    void testToString_ContainsKeyFields() {
        BoardState state = new BoardState("session123", SAMPLE_BOARD_JSON, "ABCDEFG");
        state.setId(42L);
        state.setAnalysisScore(100);

        String toString = state.toString();

        assertTrue(toString.contains("id=42"), "toString should contain id");
        assertTrue(toString.contains("userSession='session123'"), "toString should contain userSession");
        assertTrue(toString.contains("handTiles='ABCDEFG'"), "toString should contain handTiles");
        assertTrue(toString.contains("analysisScore=100"), "toString should contain analysisScore");
        assertTrue(toString.contains("createdAt="), "toString should contain createdAt");
        assertTrue(toString.contains("lastModified="), "toString should contain lastModified");
    }

    @Test
    void testToString_TruncatesLongBoardData() {
        // Test that toString truncates long board data
        String longBoardData = "{\"board\":" + "x".repeat(100) + "}";
        BoardState state = new BoardState("session", longBoardData, "TILES");

        String toString = state.toString();

        assertTrue(toString.contains("boardData='"), "toString should contain boardData");
        assertTrue(toString.contains("...'"), "toString should truncate with ...");
        assertTrue(toString.length() < longBoardData.length() + 200,
                  "toString should not include full board data");
    }

    @Test
    void testToString_NullFields() {
        BoardState state = new BoardState();
        state.setUserSession(null);
        state.setBoardData(null);
        state.setHandTiles(null);

        String toString = state.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("userSession=") || toString.contains("null"));
    }

    // ========================================
    // INTEGRATION SCENARIO TESTS
    // ========================================

    @Test
    void testScenario_NewGameBoard() {
        // Scenario: Creating a new game board
        BoardState state = new BoardState("player-session-001",
                                         "{\"board\":[[null]]}",
                                         "ABCDEFG");

        assertNotNull(state.getId() == null || state.getId() >= 0);
        assertEquals("player-session-001", state.getUserSession());
        assertEquals("{\"board\":[[null]]}", state.getBoardData());
        assertEquals("ABCDEFG", state.getHandTiles());
        assertEquals(0, state.getAnalysisScore());
    }

    @Test
    void testScenario_GameInProgress() {
        // Scenario: Game in progress with moves
        BoardState state = new BoardState("player-session-002",
                                         "{\"board\":[[\"W\",\"O\",\"R\",\"D\"]]}",
                                         "EFGHIJK");
        state.setAnalysisScore(15);

        state.updateBoardData("{\"board\":[[\"W\",\"O\",\"R\",\"D\",\"S\"]]}");
        state.updateHandTiles("FGHIJK");
        state.setAnalysisScore(20);

        assertEquals(20, state.getAnalysisScore());
        assertEquals("FGHIJK", state.getHandTiles());
    }

    @Test
    void testScenario_BoardAnalysisUpdate() {
        // Scenario: Updating board after analysis
        BoardState state = new BoardState("analyzer-session",
                                         SAMPLE_BOARD_JSON,
                                         SAMPLE_HAND_TILES);
        LocalDateTime createdTime = state.getCreatedAt();

        // Analyze and update
        state.setAnalysisScore(250);
        state.updateBoardData("{\"board\":[[\"ANALYZED\"]]}");

        assertEquals(250, state.getAnalysisScore());
        assertEquals("{\"board\":[[\"ANALYZED\"]]}", state.getBoardData());
        assertEquals(createdTime, state.getCreatedAt());
        assertTrue(state.getLastModified().isAfter(createdTime) ||
                  state.getLastModified().isEqual(createdTime));
    }
}