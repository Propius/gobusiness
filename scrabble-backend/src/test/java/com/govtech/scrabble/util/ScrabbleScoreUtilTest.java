package com.govtech.scrabble.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScrabbleScoreUtilTest {

    @Test
    void testGetLetterScore_UppercaseLetters() {
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('A'));
        assertEquals(3, ScrabbleScoreUtil.getLetterScore('B'));
        assertEquals(3, ScrabbleScoreUtil.getLetterScore('C'));
        assertEquals(2, ScrabbleScoreUtil.getLetterScore('D'));
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('E'));
        assertEquals(4, ScrabbleScoreUtil.getLetterScore('F'));
        assertEquals(2, ScrabbleScoreUtil.getLetterScore('G'));
        assertEquals(4, ScrabbleScoreUtil.getLetterScore('H'));
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('I'));
        assertEquals(8, ScrabbleScoreUtil.getLetterScore('J'));
        assertEquals(5, ScrabbleScoreUtil.getLetterScore('K'));
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('L'));
        assertEquals(3, ScrabbleScoreUtil.getLetterScore('M'));
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('N'));
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('O'));
        assertEquals(3, ScrabbleScoreUtil.getLetterScore('P'));
        assertEquals(10, ScrabbleScoreUtil.getLetterScore('Q'));
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('R'));
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('S'));
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('T'));
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('U'));
        assertEquals(4, ScrabbleScoreUtil.getLetterScore('V'));
        assertEquals(4, ScrabbleScoreUtil.getLetterScore('W'));
        assertEquals(8, ScrabbleScoreUtil.getLetterScore('X'));
        assertEquals(4, ScrabbleScoreUtil.getLetterScore('Y'));
        assertEquals(10, ScrabbleScoreUtil.getLetterScore('Z'));
    }

    @Test
    void testGetLetterScore_LowercaseLetters() {
        assertEquals(1, ScrabbleScoreUtil.getLetterScore('a'));
        assertEquals(3, ScrabbleScoreUtil.getLetterScore('b'));
        assertEquals(10, ScrabbleScoreUtil.getLetterScore('q'));
        assertEquals(10, ScrabbleScoreUtil.getLetterScore('z'));
        assertEquals(8, ScrabbleScoreUtil.getLetterScore('j'));
        assertEquals(8, ScrabbleScoreUtil.getLetterScore('x'));
        assertEquals(5, ScrabbleScoreUtil.getLetterScore('k'));
    }

    @Test
    void testGetLetterScore_InvalidCharacters() {
        assertEquals(0, ScrabbleScoreUtil.getLetterScore('0'));
        assertEquals(0, ScrabbleScoreUtil.getLetterScore('9'));
        assertEquals(0, ScrabbleScoreUtil.getLetterScore(' '));
        assertEquals(0, ScrabbleScoreUtil.getLetterScore('!'));
        assertEquals(0, ScrabbleScoreUtil.getLetterScore('@'));
        assertEquals(0, ScrabbleScoreUtil.getLetterScore('#'));
        assertEquals(0, ScrabbleScoreUtil.getLetterScore('$'));
        assertEquals(0, ScrabbleScoreUtil.getLetterScore('%'));
    }

    @Test
    void testCalculateWordScore_SimpleWord() {
        assertEquals(4, ScrabbleScoreUtil.calculateWordScore("TEST"));
        assertEquals(8, ScrabbleScoreUtil.calculateWordScore("HELLO"));
        assertEquals(9, ScrabbleScoreUtil.calculateWordScore("WORLD"));
    }

    @Test
    void testCalculateWordScore_HighScoringWords() {
        assertEquals(22, ScrabbleScoreUtil.calculateWordScore("QUIZ"));
        assertEquals(12, ScrabbleScoreUtil.calculateWordScore("ZOO"));
        assertEquals(29, ScrabbleScoreUtil.calculateWordScore("JAZZ"));
    }

    @Test
    void testCalculateWordScore_LowercaseWord() {
        assertEquals(4, ScrabbleScoreUtil.calculateWordScore("test"));
        assertEquals(8, ScrabbleScoreUtil.calculateWordScore("hello"));
        assertEquals(22, ScrabbleScoreUtil.calculateWordScore("quiz"));
    }

    @Test
    void testCalculateWordScore_MixedCase() {
        assertEquals(4, ScrabbleScoreUtil.calculateWordScore("TeSt"));
        assertEquals(8, ScrabbleScoreUtil.calculateWordScore("HeLLo"));
        assertEquals(22, ScrabbleScoreUtil.calculateWordScore("QuIz"));
    }

    @Test
    void testCalculateWordScore_EmptyString() {
        assertEquals(0, ScrabbleScoreUtil.calculateWordScore(""));
    }

    @Test
    void testCalculateWordScore_WhitespaceOnly() {
        assertEquals(0, ScrabbleScoreUtil.calculateWordScore("   "));
        assertEquals(0, ScrabbleScoreUtil.calculateWordScore("\t\n"));
    }

    @Test
    void testCalculateWordScore_NullInput() {
        assertEquals(0, ScrabbleScoreUtil.calculateWordScore(null));
    }

    @Test
    void testCalculateWordScore_WithNumbers() {
        assertEquals(4, ScrabbleScoreUtil.calculateWordScore("TEST123"));
        assertEquals(8, ScrabbleScoreUtil.calculateWordScore("HELLO456"));
    }

    @Test
    void testCalculateWordScore_WithSpecialCharacters() {
        assertEquals(4, ScrabbleScoreUtil.calculateWordScore("TEST!@#"));
        assertEquals(8, ScrabbleScoreUtil.calculateWordScore("HELLO$%^"));
    }

    @Test
    void testCalculateWordScore_WithSpaces() {
        assertEquals(4, ScrabbleScoreUtil.calculateWordScore("T E S T"));
        assertEquals(8, ScrabbleScoreUtil.calculateWordScore("H E L L O"));
    }

    @Test
    void testCalculateWordScore_VeryLongWord() {
        String longWord = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int expectedScore = 1 + 3 + 3 + 2 + 1 + 4 + 2 + 4 + 1 + 8 + 5 + 1 + 3 + 1 + 1 + 3 + 10 + 1 + 1 + 1 + 1 + 4 + 4 + 8 + 4 + 10;
        assertEquals(expectedScore, ScrabbleScoreUtil.calculateWordScore(longWord));
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_DoubleLetterScore() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("dl", "normal", "normal", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(5, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_TripleLetterScore() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("tl", "normal", "normal", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(6, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_DoubleWordScore() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("dw", "normal", "normal", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(8, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_TripleWordScore() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("tw", "normal", "normal", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(12, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_MultipleDoubleWord() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("dw", "normal", "dw", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(16, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_DoubleLetterAndTripleWord() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("dl", "normal", "tw", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(15, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_AllTileTypes() {
        String word = "QUIZ";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("dl", "tl", "dw", "tw");

        // Q=10*2=20, U=1*3=3, I=1, Z=10
        // Base: 20+3+1+10 = 34
        // Multipliers: 2*3 = 6
        // Total: 34*6 = 204
        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(204, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_FullNames() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("double_letter", "triple_letter", "double_word", "triple_word");

        // T=1*2=2, E=1*3=3, S=1, T=1
        // Base: 2+3+1+1 = 7
        // Multipliers: 2*3 = 6
        // Total: 7*6 = 42
        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(42, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_NullWord() {
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("dl", "normal", "normal", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(null, positions, specialTiles);
        assertEquals(0, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_NullPositions() {
        String word = "TEST";
        List<String> specialTiles = Arrays.asList("dl", "normal", "normal", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, null, specialTiles);
        assertEquals(4, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_NullSpecialTiles() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, null);
        assertEquals(4, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_EmptyWord() {
        List<Integer> positions = Arrays.asList();
        List<String> specialTiles = Arrays.asList();

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles("", positions, specialTiles);
        assertEquals(0, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_PositionOutOfBounds() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 100, 2, 3);
        List<String> specialTiles = Arrays.asList("dl", "normal", "normal", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(5, score);
    }

    @Test
    void testCalculateWordScoreWithSpecialTiles_InvalidSpecialTileType() {
        String word = "TEST";
        List<Integer> positions = Arrays.asList(0, 1, 2, 3);
        List<String> specialTiles = Arrays.asList("invalid", "normal", "normal", "normal");

        int score = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(word, positions, specialTiles);
        assertEquals(4, score);
    }

    @Test
    void testCalculateBoardWordScore_BasicScoring() {
        String word = "TEST";
        List<int[]> boardPositions = Arrays.asList(
                new int[]{0, 0},
                new int[]{0, 1},
                new int[]{0, 2},
                new int[]{0, 3}
        );
        String[][] specialTilesBoard = {
                {"normal", "normal", "normal", "normal"}
        };
        List<Boolean> usesHandTile = Arrays.asList(true, true, true, true);

        int score = ScrabbleScoreUtil.calculateBoardWordScore(word, boardPositions, specialTilesBoard, usesHandTile);
        assertEquals(4, score);
    }

    @Test
    void testCalculateBoardWordScore_WithSpecialTiles() {
        String word = "TEST";
        List<int[]> boardPositions = Arrays.asList(
                new int[]{0, 0},
                new int[]{0, 1},
                new int[]{0, 2},
                new int[]{0, 3}
        );
        String[][] specialTilesBoard = {
                {"dl", "normal", "dw", "normal"}
        };
        List<Boolean> usesHandTile = Arrays.asList(true, true, true, true);

        int score = ScrabbleScoreUtil.calculateBoardWordScore(word, boardPositions, specialTilesBoard, usesHandTile);
        assertEquals(10, score);
    }

    @Test
    void testCalculateBoardWordScore_ExistingTilesNoBonus() {
        String word = "TEST";
        List<int[]> boardPositions = Arrays.asList(
                new int[]{0, 0},
                new int[]{0, 1},
                new int[]{0, 2},
                new int[]{0, 3}
        );
        String[][] specialTilesBoard = {
                {"dl", "normal", "dw", "normal"}
        };
        List<Boolean> usesHandTile = Arrays.asList(false, true, false, true);

        int score = ScrabbleScoreUtil.calculateBoardWordScore(word, boardPositions, specialTilesBoard, usesHandTile);
        assertEquals(4, score);
    }

    @Test
    void testCalculateBoardWordScore_NullWord() {
        List<int[]> boardPositions = Arrays.asList(new int[]{0, 0});
        String[][] specialTilesBoard = {{"normal"}};
        List<Boolean> usesHandTile = Arrays.asList(true);

        int score = ScrabbleScoreUtil.calculateBoardWordScore(null, boardPositions, specialTilesBoard, usesHandTile);
        assertEquals(0, score);
    }

    @Test
    void testCalculateBoardWordScore_NullBoardPositions() {
        String word = "TEST";
        String[][] specialTilesBoard = {{"normal"}};
        List<Boolean> usesHandTile = Arrays.asList(true);

        int score = ScrabbleScoreUtil.calculateBoardWordScore(word, null, specialTilesBoard, usesHandTile);
        assertEquals(4, score);
    }

    @Test
    void testCalculateBoardWordScore_NullSpecialTilesBoard() {
        String word = "TEST";
        List<int[]> boardPositions = Arrays.asList(new int[]{0, 0});
        List<Boolean> usesHandTile = Arrays.asList(true);

        int score = ScrabbleScoreUtil.calculateBoardWordScore(word, boardPositions, null, usesHandTile);
        assertEquals(4, score);
    }

    @Test
    void testCalculateBoardWordScore_InvalidRowCol() {
        String word = "TEST";
        List<int[]> boardPositions = Arrays.asList(
                new int[]{-1, 0},
                new int[]{0, 1},
                new int[]{0, 2},
                new int[]{0, 3}
        );
        String[][] specialTilesBoard = {
                {"normal", "normal", "normal", "normal"}
        };
        List<Boolean> usesHandTile = Arrays.asList(true, true, true, true);

        int score = ScrabbleScoreUtil.calculateBoardWordScore(word, boardPositions, specialTilesBoard, usesHandTile);
        assertEquals(4, score);
    }

    @Test
    void testGetAllLetterScores_ReturnsAllLetters() {
        Map<Character, Integer> letterScores = ScrabbleScoreUtil.getAllLetterScores();

        assertNotNull(letterScores);
        assertEquals(26, letterScores.size());
        assertEquals(1, letterScores.get('A'));
        assertEquals(10, letterScores.get('Q'));
        assertEquals(10, letterScores.get('Z'));
    }

    @Test
    void testGetAllLetterScores_ReturnsDefensiveCopy() {
        Map<Character, Integer> letterScores1 = ScrabbleScoreUtil.getAllLetterScores();
        Map<Character, Integer> letterScores2 = ScrabbleScoreUtil.getAllLetterScores();

        assertNotSame(letterScores1, letterScores2);
        letterScores1.put('A', 999);
        assertEquals(1, letterScores2.get('A'));
    }

    @Test
    void testApplySpecialTileMultiplier_DoubleLetterShorthand() {
        ScrabbleScoreUtil.SpecialTileMultiplier multiplier = new ScrabbleScoreUtil.SpecialTileMultiplier();
        int score = ScrabbleScoreUtil.applySpecialTileMultiplier(5, "dl", multiplier);

        assertEquals(10, score);
        assertEquals(1, multiplier.getWordMultiplier());
    }

    @Test
    void testApplySpecialTileMultiplier_TripleLetterShorthand() {
        ScrabbleScoreUtil.SpecialTileMultiplier multiplier = new ScrabbleScoreUtil.SpecialTileMultiplier();
        int score = ScrabbleScoreUtil.applySpecialTileMultiplier(5, "tl", multiplier);

        assertEquals(15, score);
        assertEquals(1, multiplier.getWordMultiplier());
    }

    @Test
    void testApplySpecialTileMultiplier_DoubleWordShorthand() {
        ScrabbleScoreUtil.SpecialTileMultiplier multiplier = new ScrabbleScoreUtil.SpecialTileMultiplier();
        int score = ScrabbleScoreUtil.applySpecialTileMultiplier(5, "dw", multiplier);

        assertEquals(5, score);
        assertEquals(2, multiplier.getWordMultiplier());
    }

    @Test
    void testApplySpecialTileMultiplier_TripleWordShorthand() {
        ScrabbleScoreUtil.SpecialTileMultiplier multiplier = new ScrabbleScoreUtil.SpecialTileMultiplier();
        int score = ScrabbleScoreUtil.applySpecialTileMultiplier(5, "tw", multiplier);

        assertEquals(5, score);
        assertEquals(3, multiplier.getWordMultiplier());
    }

    @Test
    void testApplySpecialTileMultiplier_NullSpecialTileType() {
        ScrabbleScoreUtil.SpecialTileMultiplier multiplier = new ScrabbleScoreUtil.SpecialTileMultiplier();
        int score = ScrabbleScoreUtil.applySpecialTileMultiplier(5, null, multiplier);

        assertEquals(5, score);
        assertEquals(1, multiplier.getWordMultiplier());
    }

    @Test
    void testApplySpecialTileMultiplier_NullMultiplierTracker() {
        int score = ScrabbleScoreUtil.applySpecialTileMultiplier(5, "dl", null);
        // When multiplierTracker is null, method returns unchanged score
        assertEquals(5, score);
    }

    @Test
    void testApplySpecialTileMultiplier_NormalTile() {
        ScrabbleScoreUtil.SpecialTileMultiplier multiplier = new ScrabbleScoreUtil.SpecialTileMultiplier();
        int score = ScrabbleScoreUtil.applySpecialTileMultiplier(5, "normal", multiplier);

        assertEquals(5, score);
        assertEquals(1, multiplier.getWordMultiplier());
    }

    @Test
    void testApplySpecialTileMultiplier_MultipleWordMultipliers() {
        ScrabbleScoreUtil.SpecialTileMultiplier multiplier = new ScrabbleScoreUtil.SpecialTileMultiplier();

        ScrabbleScoreUtil.applySpecialTileMultiplier(5, "dw", multiplier);
        assertEquals(2, multiplier.getWordMultiplier());

        ScrabbleScoreUtil.applySpecialTileMultiplier(5, "tw", multiplier);
        assertEquals(6, multiplier.getWordMultiplier());

        ScrabbleScoreUtil.applySpecialTileMultiplier(5, "dw", multiplier);
        assertEquals(12, multiplier.getWordMultiplier());
    }

    @Test
    void testSpecialTileMultiplier_InitialState() {
        ScrabbleScoreUtil.SpecialTileMultiplier multiplier = new ScrabbleScoreUtil.SpecialTileMultiplier();
        assertEquals(1, multiplier.getWordMultiplier());
    }

    @Test
    void testSpecialTileMultiplier_ApplyMultiplier() {
        ScrabbleScoreUtil.SpecialTileMultiplier multiplier = new ScrabbleScoreUtil.SpecialTileMultiplier();

        multiplier.applyWordMultiplier(2);
        assertEquals(2, multiplier.getWordMultiplier());

        multiplier.applyWordMultiplier(3);
        assertEquals(6, multiplier.getWordMultiplier());
    }

    @ParameterizedTest
    @CsvSource({
            "A, 1",
            "B, 3",
            "C, 3",
            "D, 2",
            "E, 1",
            "F, 4",
            "G, 2",
            "H, 4",
            "I, 1",
            "J, 8",
            "K, 5",
            "L, 1",
            "M, 3",
            "N, 1",
            "O, 1",
            "P, 3",
            "Q, 10",
            "R, 1",
            "S, 1",
            "T, 1",
            "U, 1",
            "V, 4",
            "W, 4",
            "X, 8",
            "Y, 4",
            "Z, 10"
    })
    void testGetLetterScore_AllLettersParameterized(char letter, int expectedScore) {
        assertEquals(expectedScore, ScrabbleScoreUtil.getLetterScore(letter));
    }
}